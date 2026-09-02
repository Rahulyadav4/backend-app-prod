
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.taskmanager.model.Task;

class TaskTest {

    @Test
    void gettersAndSetters_shouldWork() {

        Task task = new Task();

        task.setId("task-1");
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setStatus("TODO");
        task.setVersion(1L);

        assertEquals("task-1", task.getId());
        assertEquals("Test Task", task.getTitle());
        assertEquals("Test Description", task.getDescription());
        assertEquals("TODO", task.getStatus());
        assertEquals(1L, task.getVersion());
    }
}