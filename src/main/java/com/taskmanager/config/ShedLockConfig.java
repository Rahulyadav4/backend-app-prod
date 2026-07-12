package com.taskmanager.config;

import com.mongodb.client.MongoClient;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.mongo.MongoLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// FIX: CsvKafkaLoader.load() ran on every pod independently — scaling replicas
// UP made duplicate Kafka publishes WORSE, not better. This ensures only one
// pod executes the job per interval.
@Configuration
@EnableSchedulerLock(defaultLockAtMostFor = "PT1M")
public class ShedLockConfig {

    @Bean
    public LockProvider lockProvider(MongoClient mongoClient) {
        return new MongoLockProvider(mongoClient.getDatabase("taskdb"));
    }
}