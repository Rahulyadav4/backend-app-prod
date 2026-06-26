package com.taskmanager.Kafka;

import com.taskmanager.model.Task;
import com.taskmanager.repository.TaskRepository;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.cache.Cache;

@Component
public class TaskKafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(TaskKafkaConsumer.class);
    private final TaskRepository taskRepository;
    private final CacheManager cacheManager;

    public TaskKafkaConsumer(TaskRepository taskRepository, CacheManager cacheManager) {
        this.taskRepository = taskRepository;
        this.cacheManager = cacheManager;
    }

    @KafkaListener(
        topics = "${kafka.topic.tasks}",
        groupId = "${spring.kafka.consumer.group-id}",
        concurrency = "3", // 3 parallel threads — needs >=3 partitions
        containerFactory = "batchFactory"
    )
    public void consume(List<Task> tasks, Acknowledgment ack) {
        for (Task task : tasks) {
            // Upsert ALWAYS — fixes the silently-dropped-update bug
            taskRepository.save(task);

            Cache cache = cacheManager.getCache("task");
            if (cache != null && task.getId() != null) {
                cache.evict(task.getId()); // keep Redis in sync, not just REST paths
            }
        }
        log.info("Upserted batch of {} tasks", tasks.size());
        ack.acknowledge(); // commit only after the WHOLE batch succeeds
    }
}