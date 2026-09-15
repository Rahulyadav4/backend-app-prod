package com.taskmanager.risk.model;

public class RiskDecision {

    private String taskId;
    private String decision;

    public RiskDecision() {
    }

    public RiskDecision(String taskId, String decision) {
        this.taskId = taskId;
        this.decision = decision;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }
}