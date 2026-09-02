package com.taskmanagertest;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;

import com.taskmanager.TaskManagerApplication;

class TaskManagerApplicationTest {

    @Test
    void testApplicationConstructor() {
        new TaskManagerApplication();
    }

    @Test
    void testMain() {

        try (var mockedSpringApplication =
                     mockStatic(SpringApplication.class)) {

            TaskManagerApplication.main(new String[] {});

            mockedSpringApplication.verify(
                    () -> SpringApplication.run(
                            eq(TaskManagerApplication.class),
                            eq(new String[] {})
                    )
            );
        }
    }
}
