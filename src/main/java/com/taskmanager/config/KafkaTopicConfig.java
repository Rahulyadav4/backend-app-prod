package com.taskmanager.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${kafka.topic.tasks}")
    private String topic;

    // Reads your existing kafka.topic.partitions=3 from application.properties
    // instead of duplicating the number here.
    @Value("${kafka.topic.partitions}")
    private int partitions;

    @Bean
    public NewTopic tasksTopic() {
        return TopicBuilder.name(topic)
                .partitions(partitions)
                .replicas(3)   // matches the 3-broker Kafka StatefulSet
                .build();
    }
}