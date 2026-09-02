package Kafkatest;

import static org.mockito.Mockito.*;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.taskmanager.Kafka.CsvKafkaLoader;
import com.taskmanager.Kafka.TaskKafkaProducer;

class CsvKafkaLoaderTest {

    private TaskKafkaProducer producer;
    private CsvKafkaLoader loader;

    @BeforeEach
    void setUp() {

        producer = mock(TaskKafkaProducer.class);

        loader = new CsvKafkaLoader(producer);
    }

    @Test
    void load_shouldReadCsvAndPublishTasks() throws Exception {

        Path file = Files.createTempFile(
                "tasks",
                ".csv"
        );

        Files.writeString(
                file,
                "id,title,description,status\n"
                        + "1,Task One,Description One,TODO\n"
                        + "2,Task Two,Description Two,DONE\n"
        );

        ReflectionTestUtils.setField(
                loader,
                "csvFilePath",
                file.toString()
        );

        loader.load();

        verify(producer, times(2))
                .send(any());

        Files.deleteIfExists(file);
    }

    @Test
    void load_shouldSkipBlankLines() throws Exception {

        Path file = Files.createTempFile(
                "tasks",
                ".csv"
        );

        Files.writeString(
                file,
                "id,title,description,status\n"
                        + "\n"
                        + "1,Task One,Description One,TODO\n"
                        + "\n"
        );

        ReflectionTestUtils.setField(
                loader,
                "csvFilePath",
                file.toString()
        );

        loader.load();

        verify(producer, times(1))
                .send(any());

        Files.deleteIfExists(file);
    }

    @Test
    void load_shouldSkipRowsWithLessThanFourColumns()
            throws Exception {

        Path file = Files.createTempFile(
                "tasks",
                ".csv"
        );

        Files.writeString(
                file,
                "id,title,description,status\n"
                        + "1,OnlyTwoColumns\n"
                        + "2,Task Two,Description Two,DONE\n"
        );

        ReflectionTestUtils.setField(
                loader,
                "csvFilePath",
                file.toString()
        );

        loader.load();

        verify(producer, times(1))
                .send(any());

        Files.deleteIfExists(file);
    }

    @Test
    void load_shouldHandleMissingFile() {

        ReflectionTestUtils.setField(
                loader,
                "csvFilePath",
                "does-not-exist.csv"
        );

        loader.load();

        verify(producer, never())
                .send(any());
    }
}
