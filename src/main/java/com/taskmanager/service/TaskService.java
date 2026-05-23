package com.taskmanager.service;

import com.taskmanager.model.Task;
import com.taskmanager.repository.TaskRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TaskService {

    private final TaskRepository repo;

    public TaskService(TaskRepository repo) {
        this.repo = repo;
    }

    public String createTask(Task task) {
        repo.save(task);
        return task.getId();
    }

    // FIX SDE-2: Circuit breaker wraps MongoDB call
    // FIX SDE-2: @Cacheable now hits Redis (spring.cache.type=redis)
    @CircuitBreaker(name = "mongoBreaker", fallbackMethod = "getTaskFallback")
    @Cacheable(value = "task", key = "#id")
    public Task getTask(String id) {
        // FIX SDE-1: 404 instead of null body
        return repo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Task not found: " + id));
    }

    public Task getTaskFallback(String id, Exception e) {
        throw new ResponseStatusException(
            HttpStatus.SERVICE_UNAVAILABLE,
            "Service temporarily unavailable. Try again shortly.");
    }

    // FIX SDE-1: @CacheEvict — clears Redis entry on update
    @CacheEvict(value = "task", key = "#task.id")
    public String updateTask(Task task) {
        repo.save(task);
        return "Updated";
    }

    // FIX SDE-1: @CacheEvict — clears Redis entry on delete
    @CacheEvict(value = "task", key = "#id")
    public String deleteTask(String id) {
        repo.deleteById(id);
        return "Deleted";
    }
}
