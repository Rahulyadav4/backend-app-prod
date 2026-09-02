package com.taskmanager.repository;

import com.taskmanager.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * NOTE: field names below (title, description, status) are inferred from the
 * "id, title, description, status" CSV format documented in the project README.
 * If your actual Task model uses different field/getter names, rename the
 * setTitle/getTitle/setStatus/getStatus calls to match before this compiles.
 */
@DataMongoTest
@Testcontainers
class TaskRepositoryTest {

    @Container
    static MongoDBContainer mongoDBContainer =
            new MongoDBContainer(DockerImageName.parse("mongo:6.0"));

    @DynamicPropertySource
    static void setMongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    void cleanUp() {
        taskRepository.deleteAll();
    }

    @Test
    void testSaveAndFindById() {
        Task task = new Task();
        task.setId("1");
        task.setTitle("Write report");
        task.setDescription("Quarterly summary");
        task.setStatus("PENDING");

        taskRepository.save(task);

        Optional<Task> found = taskRepository.findById("1");

        assertTrue(found.isPresent());
        assertEquals("Write report", found.get().getTitle());
        assertEquals("PENDING", found.get().getStatus());
    }

    @Test
    void testFindByIdReturnsEmptyWhenTaskDoesNotExist() {
        Optional<Task> found = taskRepository.findById("does-not-exist");

        assertTrue(found.isEmpty());
    }

    @Test
    void testUpdateExistingTask() {
        Task task = new Task();
        task.setId("2");
        task.setTitle("Draft proposal");
        task.setStatus("PENDING");
        taskRepository.save(task);

        Task fetched = taskRepository.findById("2").orElseThrow();
        fetched.setStatus("COMPLETED");
        taskRepository.save(fetched);

        Task result = taskRepository.findById("2").orElseThrow();
        assertEquals("COMPLETED", result.getStatus());
    }

    @Test
    void testDeleteById() {
        Task task = new Task();
        task.setId("3");
        task.setTitle("Temporary task");
        taskRepository.save(task);

        assertTrue(taskRepository.findById("3").isPresent());

        taskRepository.deleteById("3");

        assertTrue(taskRepository.findById("3").isEmpty());
    }

    @Test
    void testFindAllReturnsAllSavedTasks() {
        Task first = new Task();
        first.setId("4");
        first.setTitle("Task A");

        Task second = new Task();
        second.setId("5");
        second.setTitle("Task B");

        taskRepository.save(first);
        taskRepository.save(second);

        List<Task> tasks = taskRepository.findAll();

        assertEquals(2, tasks.size());
    }
}