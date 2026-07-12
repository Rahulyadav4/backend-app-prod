package com.taskmanager.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Document(collection = "tasks")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Task implements Serializable {

    @Id
    private String id;

    @NotBlank(message = "title is required")
    @Size(max = 200)
    private String title;

    @Size(max = 2000)
    private String description;

    @NotBlank(message = "status is required")
    @Pattern(regexp = "TODO|IN_PROGRESS|DONE|PENDING|COMPLETED|CANCELLED", message = "status must be TODO, IN_PROGRESS, or DONE")
    private String status;

    @Version
    private Long version;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}