package com.taskmanager.service;

import com.taskmanager.model.Task;
import com.taskmanager.outbox.model.OutBoxEvent;
import com.taskmanager.model.*;
import java.util.Optional;
import com.taskmanager.repository.TaskRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.validation.Valid;
import taskmanager.outbox.repository.OutBoxRepository;

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
    private final OutBoxRepository outboxrepository;

    public TaskService(TaskRepository repo,OutBoxRepository outboxrepository) {
        this.repo = repo;
		this.outboxrepository = outboxrepository;
    }

    public Task createTask1(
            Task task,
            String idempotencyKey) {

        Optional<Task> existing =
                repo.findByIdempotencyKey(idempotencyKey);

        if (existing.isPresent()) {
            return existing.get();
        }

        task.setIdempotencyKey(idempotencyKey);

        Task saved = repo.save(task);

        OutBoxEvent event =
                new OutBoxEvent(
                        saved.getId(),
                        "TASK_CREATED"
                );

        outboxrepository.save(event);

        return saved;
    }

    public Page<Task> listTasks(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @CircuitBreaker(name = "mongoBreaker", fallbackMethod = "getTaskFallback")
    @Cacheable(value = "task", key = "#id")
    public Task getTask(String id) {
        return repo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Task not found: " + id));
    }

    public Task getTaskFallback(String id, Exception e) {
        throw new ResponseStatusException(
            HttpStatus.SERVICE_UNAVAILABLE,
            "Service temporarily unavailable. Try again shortly.");
    }

    @CacheEvict(value = "task", key = "#task.id")
    public String updateTask(Task task) {
        try {
            repo.save(task);
        } catch (OptimisticLockingFailureException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Task was modified concurrently — refetch and retry.");
        }
        return "Updated";
    }

    @CacheEvict(value = "task", key = "#id")
    public String deleteTask(String id) {
        repo.deleteById(id);
        return "Deleted";
    }

	public Task createTask(@Valid Task task, String idempotencyKey) {
		// TODO Auto-generated method stub
		return null;
	}
}