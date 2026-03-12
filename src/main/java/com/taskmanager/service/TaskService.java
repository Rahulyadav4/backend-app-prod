package com.taskmanager.service;

import com.taskmanager.model.Task;
import com.taskmanager.repository.TaskRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@Service
public class TaskService {

 private final TaskRepository repo;

 public TaskService(TaskRepository repo){
  this.repo = repo;
 }

 public String createTask(Task task){
  repo.save(task);
  return task.getId();
 }

 public Task getTask(String id){
  return repo.findById(id).orElse(null);
 }

 public String updateTask(Task task){
  repo.save(task);
  return "Updated";
 }

 public String deleteTask(String id){
  repo.deleteById(id);
  return "Deleted";
 }
}