package com.taskmanager.controller;

import com.taskmanager.model.Task;
import com.taskmanager.service.TaskService;

import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class TaskControllerTest {

    @Test
    void testCreateTask() {

        TaskService service = mock(TaskService.class);
        TaskController controller = new TaskController(service);

        Task task = new Task();
        when(service.createTask(task)).thenReturn("123");

        String result = controller.create(task);

        assertEquals("123", result);
    }
}