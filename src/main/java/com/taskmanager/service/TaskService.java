package com.taskmanager.service;

import com.taskmanager.model.Task;
import com.taskmanager.repository.TaskRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import org.springframework.dao.OptimisticLockingFailureException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.http.HttpStatus;

import org.springframework.stereotype.Service;

import org.springframework.web.server.ResponseStatusException;

@Service
public class TaskService {

    private final TaskRepository repo;

    public TaskService(TaskRepository repo) {
        this.repo = repo;
    }

    // CREATE
    public String createTask(Task task) {

        Task saved = repo.save(task);

        return saved.getId();
    }

    // LIST
    public Page<Task> listTasks(Pageable pageable) {

        return repo.findAll(pageable);
    }

    // GET ONE
    @CircuitBreaker(
            name = "mongoBreaker",
            fallbackMethod = "getTaskFallback"
    )
    @Cacheable(
            value = "task",
            key = "#id"
    )
    public Task getTask(String id) {

        return repo.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Task not found: " + id
                        )
                );
    }

    // CIRCUIT BREAKER FALLBACK
    public Task getTaskFallback(
            String id,
            Exception e) {

        throw new ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Service temporarily unavailable. Try again shortly."
        );
    }

    // UPDATE
    @CacheEvict(
            value = "task",
            key = "#task.id"
    )
    public String updateTask(Task task) {

        try {

            repo.save(task);

        } catch (OptimisticLockingFailureException e) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Task was modified concurrently — fetch and retry."
            );
        }

        return "Updated";
    }

    // DELETE
    @CacheEvict(
            value = "task",
            key = "#id"
    )
    public String deleteTask(String id) {

        repo.deleteById(id);

        return "Deleted";
    }
}