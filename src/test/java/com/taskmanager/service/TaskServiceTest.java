package com.taskmanager.service;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import com.taskmanager.model.Task;
import com.taskmanager.repository.TaskRepository;

public class TaskServiceTest {

    @Test
    void testCreateTask() {

        TaskRepository repo = mock(TaskRepository.class);
        TaskService service = new TaskService(repo);

        Task task = new Task();
        task.setId("1");

        when(repo.save(task)).thenReturn(task);

        String result = service.createTask(task);

        assertEquals("1", result);
    }

    @Test
    void testGetTask() {

        TaskRepository repo = mock(TaskRepository.class);
        TaskService service = new TaskService(repo);

        Task task = new Task();
        task.setId("1");

        when(repo.findById("1")).thenReturn(Optional.of(task));

        Task result = service.getTask("1");

        assertEquals("1", result.getId());
    }

}