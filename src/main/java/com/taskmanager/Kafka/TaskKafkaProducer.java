package com.taskmanager.Kafka;

import com.taskmanager.model.Task;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class TaskKafkaProducer {

    private final KafkaTemplate<String, Task> kafkaTemplate;

    @Value("${kafka.topic.tasks}")
    private String topic;

    public TaskKafkaProducer(KafkaTemplate<String, Task> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(Task task) {
        kafkaTemplate.send(topic, task);
    }
}
