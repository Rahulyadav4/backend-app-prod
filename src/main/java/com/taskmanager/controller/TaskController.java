package com.taskmanager.controller;

import com.taskmanager.model.Task;
import com.taskmanager.service.TaskService;

import jakarta.validation.Valid;

import org.springframework.data.domain.PageRequest;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // CONTROL ENDPOINT
    @GetMapping("/control")
    public ResponseEntity<String> control() {

        return ResponseEntity.ok("working");
    }

    // CREATE
    @PostMapping
    public ResponseEntity<String> create(
            @Valid @RequestBody Task task) {

        return ResponseEntity.ok(
                taskService.createTask(task)
        );
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<?> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(
                taskService.listTasks(
                        PageRequest.of(
                                page,
                                Math.min(size, 100)
                        )
                )
        );
    }

    // GET ONE
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTask(
            @PathVariable String id) {

        return ResponseEntity.ok(
                taskService.getTask(id)
        );
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<String> update(
            @PathVariable String id,
            @Valid @RequestBody Task task) {

        task.setId(id);

        return ResponseEntity.ok(
                taskService.updateTask(task)
        );
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(
            @PathVariable String id) {

        return ResponseEntity.ok(
                taskService.deleteTask(id)
        );
    }
}