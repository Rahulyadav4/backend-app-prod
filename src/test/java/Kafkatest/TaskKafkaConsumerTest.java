package Kafkatest;

import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.kafka.support.Acknowledgment;

import com.taskmanager.Kafka.TaskKafkaConsumer;
import com.taskmanager.model.Task;
import com.taskmanager.repository.TaskRepository;

class TaskKafkaConsumerTest {

    private TaskRepository taskRepository;
    private CacheManager cacheManager;
    private Cache cache;
    private Acknowledgment acknowledgment;

    private TaskKafkaConsumer consumer;

    @BeforeEach
    void setUp() {

        taskRepository = mock(TaskRepository.class);
        cacheManager = mock(CacheManager.class);
        cache = mock(Cache.class);
        acknowledgment = mock(Acknowledgment.class);

        consumer = new TaskKafkaConsumer(
                taskRepository,
                cacheManager
        );
    }

    @Test
    void consume_shouldSaveAllTasksAndAcknowledge() {

        Task task1 = new Task();
        Task task2 = new Task();

        task1.setId("1");
        task2.setId("2");

        List<Task> tasks = Arrays.asList(task1, task2);

        when(cacheManager.getCache("task"))
                .thenReturn(cache);

        consumer.consume(tasks, acknowledgment);

        verify(taskRepository, times(1))
                .save(task1);

        verify(taskRepository, times(1))
                .save(task2);

        verify(cache, times(1))
                .evict("1");

        verify(cache, times(1))
                .evict("2");

        verify(acknowledgment, times(1))
                .acknowledge();
    }

    @Test
    void consume_shouldHandleNullCache() {

        Task task = new Task();
        task.setId("1");

        List<Task> tasks = Arrays.asList(task);

        when(cacheManager.getCache("task"))
                .thenReturn(null);

        consumer.consume(tasks, acknowledgment);

        verify(taskRepository, times(1))
                .save(task);

        verify(acknowledgment, times(1))
                .acknowledge();
    }

    @Test
    void consume_shouldHandleNullTaskId() {

        Task task = new Task();

        List<Task> tasks = Arrays.asList(task);

        when(cacheManager.getCache("task"))
                .thenReturn(cache);

        consumer.consume(tasks, acknowledgment);

        verify(taskRepository, times(1))
                .save(task);

        verify(cache, never())
                .evict(any());

        verify(acknowledgment, times(1))
                .acknowledge();
    }
}