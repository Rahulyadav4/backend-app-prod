package service;

import model.Task;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class TaskService {

    private final Firestore firestore;

    public TaskService(Firestore firestore) {
        this.firestore = firestore;
    }

    // CREATE
    public String createTask(Task task) throws Exception {
        DocumentReference docRef = firestore.collection("tasks").document();
        task.setId(docRef.getId());
        docRef.set(task);
        return task.getId();
    }

    // READ
    public Task getTask(String id) throws Exception {
        DocumentSnapshot snapshot = firestore.collection("tasks")
                .document(id)
                .get()
                .get();

        if (snapshot.exists()) {
            return snapshot.toObject(Task.class);
        }
        return null;
    }

    // UPDATE
    public String updateTask(Task task) throws Exception {
        firestore.collection("tasks")
                .document(task.getId())
                .set(task);
        return "Updated";
    }

    // DELETE
    public String deleteTask(String id) {
        firestore.collection("tasks").document(id).delete();
        return "Deleted";
    }
}