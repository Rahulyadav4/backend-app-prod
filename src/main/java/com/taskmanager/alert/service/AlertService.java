package com.taskmanager.alert.service;

import com.taskmanager.alert.model.Alert.Alert;
import com.taskmanager.alert.repository.AlertRepository;
import com.taskmanager.model.Task;
import com.taskmanager.risk.model.RiskDecision;
import org.springframework.stereotype.Service;

@Service
public class AlertService {

    private final AlertRepository alertRepository;

    public AlertService(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    public void createAlert(
            Task task,
            RiskDecision decision) {

        Alert alert = new Alert(
                task.getId(),
                decision.getDecision()
        );

        alertRepository.save(alert);
    }
}