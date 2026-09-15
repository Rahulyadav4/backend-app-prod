package com.taskmanager.Kafka;

import com.taskmanager.model.Task;
import com.taskmanager.repository.TaskRepository;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TaskKafkaConsumer {

    private final TaskRepository taskRepository;
    private final CacheManager cacheManager;

    public TaskKafkaConsumer(
            TaskRepository taskRepository,
            CacheManager cacheManager) {

        this.taskRepository = taskRepository;
        this.cacheManager = cacheManager;
    }

    @KafkaListener(
            topics = "${kafka.topic.tasks}",
            groupId = "${spring.kafka.consumer.group-id}",
            concurrency = "3",
            containerFactory = "batchFactory"
    )
    public void consume(
            List<Task> tasks,
            Acknowledgment ack) {

        for (Task task : tasks) {

            // Save task
            taskRepository.save(task);

            // Evict cache only when Task ID exists
            Cache cache =
                    cacheManager.getCache("task");

            if (cache != null && task.getId() != null) {

                cache.evict(task.getId());
            }

            System.out.println(
                    "Processed task: " + task.getId()
            );
        }

        // Manual acknowledgement
        ack.acknowledge();

        System.out.println(
                "Processed batch: " + tasks.size()
        );
    }
}