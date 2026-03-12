package com.taskmanager.controller;

import org.springframework.web.bind.annotation.*;
import com.taskmanager.model.Task;
import com.taskmanager.service.TaskService;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // health check
    @GetMapping("/control")
    public String control() {
        return "working";
    }

    // CREATE
    @PostMapping
    public String create(@RequestBody Task task) {
        return taskService.createTask(task);
    }

    // READ
    @GetMapping("/{id}")
    public Task getTask(@PathVariable("id") String id) {
        return taskService.getTask(id);
    }

    // UPDATE
    @PutMapping("/{id}")
    public String update(@PathVariable("id") String id,
                         @RequestBody Task task) {
        task.setId(id);
        return taskService.updateTask(task);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") String id) {
        return taskService.deleteTask(id);
    }
}