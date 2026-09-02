package com.taskmanager.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.Test;

import org.springframework.test.util.ReflectionTestUtils;

class KafkaTopicConfigTest {

    @Test
    void taskTopic_createsTopicWithConfiguredValues() {

        KafkaTopicConfig config = new KafkaTopicConfig();

        ReflectionTestUtils.setField(config, "topic", "task");
        ReflectionTestUtils.setField(config, "partitions", 3);

        NewTopic result = config.tasksTopic();

        assertNotNull(result);
        assertEquals("task", result.name());
        assertEquals(3, result.numPartitions());
        assertEquals(3, result.replicationFactor());
    }
}