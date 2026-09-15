package com.taskmanager.Kafka;

import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import com.taskmanager.alert.service.AlertService;
import com.taskmanager.model.Task;
import com.taskmanager.repository.TaskRepository;
import com.taskmanager.risk.client.RiskClient;
import com.taskmanager.risk.model.RiskDecision;

@Component
public class TaskKafkaConsumer {

    private final TaskRepository taskRepository;
    private final RiskClient riskClient;
    private final AlertService alertService;

    public TaskKafkaConsumer(
            TaskRepository taskRepository,
            RiskClient riskClient,
            AlertService alertService) {

        this.taskRepository = taskRepository;
        this.riskClient = riskClient;
        this.alertService = alertService;
    }

    @org.springframework.kafka.annotation.RetryableTopic(
            attempts = "3",
            backoff = @Backoff(
                    delay = 1000,
                    multiplier = 2.0
            )
    )
    @KafkaListener(
            topics = "${kafka.topic.tasks}",
            groupId = "${spring.kafka.consumer.group-id}",
            concurrency = "3",
            containerFactory = "batchFactory"
    )
    public void consume(List<Task> tasks) {

        for (Task task : tasks) {

            // 1. Store transaction/work item
            taskRepository.save(task);

            // 2. Ask Risk Service
            RiskDecision decision =
                    riskClient.evaluate(task);

            // 3. Generate alert if risky
            if ("HIGH_RISK".equals(
                    decision.getDecision())) {

                alertService.createAlert(
                        task,
                        decision
                );
            }
        }
    }

    @org.springframework.kafka.annotation.DltHandler
    public void handleDlt(Task task) {

        System.err.println(
                "Task moved to DLT: " + task.getId()
        );
    }
}