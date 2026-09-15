package taskmanager.outbox.service;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.taskmanager.Kafka.TaskKafkaProducer;
import com.taskmanager.model.Task;
import com.taskmanager.outbox.model.OutBoxEvent;
import com.taskmanager.repository.TaskRepository;

import taskmanager.outbox.repository.OutBoxRepository;

@Service
public class OutboxPublisher {

    private final OutBoxRepository outboxRepository;
    private final TaskRepository taskRepository;
    private final TaskKafkaProducer producer;

    public OutboxPublisher(
            OutBoxRepository outboxRepository,
            TaskRepository taskRepository,
            TaskKafkaProducer producer) {

        this.outboxRepository = outboxRepository;
        this.taskRepository = taskRepository;
        this.producer = producer;
    }

    @Scheduled(fixedDelay = 5000)
    public void publishEvents() {

        List<OutBoxEvent> events =
                outboxRepository.findByPublishedFalse();

        for (OutBoxEvent event : events) {

            Task task =
                    taskRepository
                            .findById(event.getTaskId())
                            .orElse(null);

            if (task == null) {
                continue;
            }

            producer.send(task);

            event.setPublished(true);

            outboxRepository.save(event);
        }
    }
}