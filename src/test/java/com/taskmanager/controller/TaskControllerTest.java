package com.taskmanager.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;

import com.taskmanager.model.Task;
import com.taskmanager.service.TaskService;

import java.util.List;

class TaskControllerTest {

    private TaskService taskService;
    private TaskController taskController;

    @BeforeEach
    void setUp() {
        taskService = mock(TaskService.class);
        taskController = new TaskController(taskService);
    }

    // ---------------------------------------------------------
    // CONTROL ENDPOINT
    // ---------------------------------------------------------

    @Test
    void testControl() {

        ResponseEntity<String> response = taskController.control();

        assertEquals(200, response.getStatusCode().value());
        assertEquals("working", response.getBody());
    }

    // ---------------------------------------------------------
    // CREATE
    // ---------------------------------------------------------

    @Test
    void testCreate() {

        Task task = mock(Task.class);

        when(taskService.createTask(task))
                .thenReturn("task-created");

        ResponseEntity<String> response =
                taskController.create(task);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("task-created", response.getBody());

        verify(taskService, times(1))
                .createTask(task);
    }

    // ---------------------------------------------------------
    // GET TASK
    // ---------------------------------------------------------

    @Test
    void testGetTask() {

        String id = "task-123";

        Task task = mock(Task.class);

        when(taskService.getTask(id))
                .thenReturn(task);

        ResponseEntity<Task> response =
                taskController.getTask(id);

        assertEquals(200, response.getStatusCode().value());
        assertSame(task, response.getBody());

        verify(taskService, times(1))
                .getTask(id);
    }

    // ---------------------------------------------------------
    // UPDATE
    // ---------------------------------------------------------

    @Test
    void testUpdate() {

        String id = "task-123";

        Task task = mock(Task.class);

        when(taskService.updateTask(task))
                .thenReturn("task-updated");

        ResponseEntity<String> response =
                taskController.update(id, task);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("task-updated", response.getBody());

        verify(task, times(1))
                .setId(id);

        verify(taskService, times(1))
                .updateTask(task);
    }

    // ---------------------------------------------------------
    // DELETE
    // ---------------------------------------------------------

    @Test
    void testDelete() {

        String id = "task-123";

        when(taskService.deleteTask(id))
                .thenReturn("task-deleted");

        ResponseEntity<String> response =
                taskController.delete(id);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("task-deleted", response.getBody());

        verify(taskService, times(1))
                .deleteTask(id);
    }

    // ---------------------------------------------------------
    // LIST
    // ---------------------------------------------------------

    @Test
    void testList() {

        int page = 0;
        int size = 20;

        Task task = mock(Task.class);

        Page<Task> taskPage =
                new PageImpl<>(List.of(task));

        when(taskService.listTasks(any()))
                .thenReturn(taskPage);

        ResponseEntity<Page<Task>> response =
                taskController.list(page, size);

        assertEquals(200, response.getStatusCode().value());
        assertSame(taskPage, response.getBody());

        verify(taskService, times(1))
                .listTasks(any());
    }

    // ---------------------------------------------------------
    // LIST - VERIFY PAGINATION VALUES
    // ---------------------------------------------------------

    @Test
    void testListWithPaginationValues() {

        int page = 2;
        int size = 10;

        Page<Task> taskPage =
                new PageImpl<>(List.of());

        when(taskService.listTasks(any()))
                .thenReturn(taskPage);

        ResponseEntity<Page<Task>> response =
                taskController.list(page, size);

        assertEquals(200, response.getStatusCode().value());
        assertSame(taskPage, response.getBody());

        ArgumentCaptor<org.springframework.data.domain.Pageable>
                captor =
                ArgumentCaptor.forClass(
                        org.springframework.data.domain.Pageable.class);

        verify(taskService)
                .listTasks(captor.capture());

        assertEquals(2, captor.getValue().getPageNumber());
        assertEquals(10, captor.getValue().getPageSize());
    }
}