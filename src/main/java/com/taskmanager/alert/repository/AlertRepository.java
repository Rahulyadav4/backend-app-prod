package com.taskmanager.alert.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.taskmanager.alert.model.Alert.Alert;

public interface AlertRepository
        extends MongoRepository<Alert, String> {
}