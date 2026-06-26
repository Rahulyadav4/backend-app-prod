package com.taskmanager.Kafka;

import com.taskmanager.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


@Component
public class CsvKafkaLoader {

    private static final Logger log = LoggerFactory.getLogger(CsvKafkaLoader.class);
    private final TaskKafkaProducer producer;
    private final String csvFilePath;

    // NOTE: in-memory only — lost on restart. For production,
    // persist this to Redis/a checkpoint file/Mongo instead.
    private long linesAlreadyRead = 0;

    public CsvKafkaLoader(TaskKafkaProducer producer,
                           @Value("${csv.file.path}") String csvFilePath) {
        this.producer = producer;
        this.csvFilePath = csvFilePath;
    }

    @Scheduled(fixedDelayString = "${csv.poll.interval:30000}")
    public void load() {
        log.info("Loading CSV: {} (resuming after line {})", csvFilePath, linesAlreadyRead);

        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            long currentLine = 0;
            boolean header = true;
            int published = 0;

            while ((line = reader.readLine()) != null) {
                currentLine++;
                if (header) { header = false; continue; }
                if (currentLine <= linesAlreadyRead) continue; // skip already-seen rows

                String[] cols = line.split(",", -1);
                if (cols.length < 4) {
                    log.warn("Malformed row at line {}: {}", currentLine, line);
                    continue;
                }

                Task task = new Task();
                String id = cols[0].trim();
                if (!id.isEmpty()) task.setId(id);
                task.setTitle(cols[1].trim());
                task.setDescription(cols[2].trim());
                task.setStatus(cols[3].trim());

                producer.send(task);
                published++;
            }

            linesAlreadyRead = currentLine;
            log.info("Published {} new rows. Total lines read: {}", published, linesAlreadyRead);

        } catch (IOException e) {
            log.error("CSV read failed: {}", csvFilePath, e);
        }
    }
}