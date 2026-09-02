
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;

import com.taskmanager.configr.KafkaConsumerConfig;
import com.taskmanager.model.Task;

class KafkaConsumerConfigTest {

    @Test
    void batchFactory_shouldConfigureConsumerFactoryAndBatchMode() {

        KafkaConsumerConfig config = new KafkaConsumerConfig();

        @SuppressWarnings("unchecked")
        ConsumerFactory<String, Task> consumerFactory =
                mock(ConsumerFactory.class);

        ConcurrentKafkaListenerContainerFactory<String, Task> result =
                config.batchFactory(consumerFactory);

        assertNotNull(result);
    }
}