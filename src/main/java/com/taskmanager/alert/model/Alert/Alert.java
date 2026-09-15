package com.taskmanager.alert.model.Alert;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "alerts")
public class Alert {

    @Id
    private String id;

    private String taskId;

    private String decision;

    private Instant createdAt;

    public Alert() {
    }

    public Alert(
            String taskId,
            String decision) {

        this.taskId = taskId;
        this.decision = decision;
        this.createdAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getDecision() {
        return decision;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}