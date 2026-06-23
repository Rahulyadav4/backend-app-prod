package com.taskmanager.Kafka;

import com.taskmanager.model.Task;
import com.taskmanager.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TaskKafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(TaskKafkaConsumer.class);

    private final TaskRepository taskRepository;

    public TaskKafkaConsumer(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @KafkaListener(topics = "${kafka.topic.tasks}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(Task task) {
        if (task.getId() == null || !taskRepository.existsById(task.getId())) {
            taskRepository.save(task);
            log.info("Saved task: title={}", task.getTitle());
        } else {
            log.debug("Skipped duplicate task id={}", task.getId());
        }
    }

}

