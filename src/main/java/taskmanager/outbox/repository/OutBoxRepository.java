package taskmanager.outbox.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.taskmanager.outbox.model.OutBoxEvent;

public interface OutBoxRepository
        extends MongoRepository<OutBoxEvent, String> {

    List<OutBoxEvent> findByPublishedFalse();
}