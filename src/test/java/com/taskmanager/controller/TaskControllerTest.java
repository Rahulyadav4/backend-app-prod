package com.taskmanager.controller;

import com.taskmanager.model.Task;
import com.taskmanager.service.TaskService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TaskControllerTest {

    private TaskService taskService;
    private TaskController taskController;

    @BeforeEach
    void setUp() {

        taskService = mock(TaskService.class);

        taskController =
                new TaskController(taskService);
    }

    // =========================================================
    // CONTROL ENDPOINT
    // =========================================================

    @Test
    void testControl() {

        ResponseEntity<String> response =
                taskController.control();

        assertEquals(
                200,
                response.getStatusCode().value()
        );

        assertEquals(
                "working",
                response.getBody()
        );
    }

    // =========================================================
    // CREATE
    // =========================================================

    @Test
    void testCreate() {

        Task task = new Task();

        when(taskService.createTask(task))
                .thenReturn("1");

        ResponseEntity<String> response =
                taskController.create(task);

        assertEquals(
                200,
                response.getStatusCode().value()
        );

        assertEquals(
                "1",
                response.getBody()
        );

        verify(taskService, times(1))
                .createTask(task);
    }

    // =========================================================
    // GET TASK
    // =========================================================

    @Test
    void testGetTask() {

        String id = "task-123";

        Task task = new Task();
        task.setId(id);

        when(taskService.getTask(id))
                .thenReturn(task);

        ResponseEntity<Task> response =
                taskController.getTask(id);

        assertEquals(
                200,
                response.getStatusCode().value()
        );

        assertEquals(
                task,
                response.getBody()
        );

        verify(taskService, times(1))
                .getTask(id);
    }

    // =========================================================
    // GET ALL TASKS
    // =========================================================

    @Test
    void testList() {

        Task task = new Task();
        task.setId("1");

        Page<Task> page =
                new PageImpl<>(
                        List.of(task)
                );

        PageRequest pageable =
                PageRequest.of(0, 20);

        when(taskService.listTasks(pageable))
                .thenReturn(page);

        ResponseEntity<?> response =
                taskController.list(0, 20);

        assertEquals(
                200,
                response.getStatusCode().value()
        );

        assertEquals(
                page,
                response.getBody()
        );

        verify(taskService, times(1))
                .listTasks(pageable);
    }

    // =========================================================
    // GET ALL - SIZE LIMITED TO 100
    // =========================================================

    @Test
    void testListLimitsSizeTo100() {

        Page<Task> page =
                new PageImpl<>(
                        List.of()
                );

        PageRequest pageable =
                PageRequest.of(0, 100);

        when(taskService.listTasks(pageable))
                .thenReturn(page);

        ResponseEntity<?> response =
                taskController.list(0, 500);

        assertEquals(
                200,
                response.getStatusCode().value()
        );

        assertEquals(
                page,
                response.getBody()
        );

        verify(taskService, times(1))
                .listTasks(pageable);
    }

    // =========================================================
    // UPDATE
    // =========================================================

    @Test
    void testUpdate() {

        String id = "task-123";

        Task task = new Task();

        when(taskService.updateTask(task))
                .thenReturn("Updated");

        ResponseEntity<String> response =
                taskController.update(id, task);

        assertEquals(
                200,
                response.getStatusCode().value()
        );

        assertEquals(
                "Updated",
                response.getBody()
        );

        assertEquals(
                id,
                task.getId()
        );

        verify(taskService, times(1))
                .updateTask(task);
    }

    // =========================================================
    // DELETE
    // =========================================================

    @Test
    void testDelete() {

        String id = "task-123";

        when(taskService.deleteTask(id))
                .thenReturn("Deleted");

        ResponseEntity<String> response =
                taskController.delete(id);

        assertEquals(
                200,
                response.getStatusCode().value()
        );

        assertEquals(
                "Deleted",
                response.getBody()
        );

        verify(taskService, times(1))
                .deleteTask(id);
    }
}