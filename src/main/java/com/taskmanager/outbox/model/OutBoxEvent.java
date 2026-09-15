package com.taskmanager.outbox.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "outbox_events")
public class OutBoxEvent {

    @Id
    private String id;

    private String taskId;

    private String eventType;

    private boolean published;

    private Instant createdAt;

    public OutBoxEvent() {
    }

    public OutBoxEvent(
            String taskId,
            String eventType) {

        this.taskId = taskId;
        this.eventType = eventType;
        this.published = false;
        this.createdAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getEventType() {
        return eventType;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }
}