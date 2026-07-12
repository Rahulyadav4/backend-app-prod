package com.taskmanager.Kafka;

import com.taskmanager.model.Task;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
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

    @Value("${csv.file.path}")
    private String csvFilePath;

    public CsvKafkaLoader(TaskKafkaProducer producer) {
        this.producer = producer;
    }

    @Scheduled(fixedDelayString = "${csv.poll.interval:30000}")
    @SchedulerLock(name = "csvKafkaLoader_load", lockAtMostFor = "PT1M", lockAtLeastFor = "PT10S")
    public void load() {
        log.info("Loading tasks from CSV: {}", csvFilePath);
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            boolean header = true;
            while ((line = reader.readLine()) != null) {
                if (header) { header = false; continue; }
                String[] cols = line.split(",", -1);
                if (cols.length < 4) continue;

                Task task = new Task();
                String id = cols[0].trim();
                if (!id.isEmpty()) task.setId(id);
                task.setTitle(cols[1].trim());
                task.setDescription(cols[2].trim());
                task.setStatus(cols[3].trim());

                producer.send(task);
                log.info("Published to Kafka: title={}", task.getTitle());
            }
        } catch (IOException e) {
            log.error("Failed to read CSV file: {}", csvFilePath, e);
        }
    }
}