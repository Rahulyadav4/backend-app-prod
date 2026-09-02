package Kafkatest;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import com.taskmanager.Kafka.TaskKafkaProducer;
import com.taskmanager.model.Task;

class TaskKafkaProducerTest {

    private KafkaTemplate<String, Task> kafkaTemplate;
    private TaskKafkaProducer producer;

    @BeforeEach
    void setUp() {
        kafkaTemplate = org.mockito.Mockito.mock(KafkaTemplate.class);

        producer = new TaskKafkaProducer(kafkaTemplate);

        ReflectionTestUtils.setField(
                producer,
                "topic",
                "task-topic"
        );
    }

    @Test
    void send_shouldPublishTaskToKafka() {

        Task task = new Task();

        producer.send(task);

        verify(kafkaTemplate, times(1))
                .send("task-topic", task);
    }
}
