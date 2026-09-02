package com.taskmanager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.taskmanager.model.Task;
import com.taskmanager.repository.TaskRepository;

public class TaskServiceTest {

    private static final List<String> RESULTS = new ArrayList<>();

    private static LocalDateTime startTime;

    @BeforeAll
    static void beforeAllTests() {
        startTime = LocalDateTime.now();
        RESULTS.clear();
    }

    // ============================================================
    // 1. CREATE TASK - SUCCESS
    // ============================================================

    @Test
    void testCreateTask() {

        TaskRepository repo = mock(TaskRepository.class);
        TaskService service = new TaskService(repo);

        Task task = new Task();
        task.setId("1");

        when(repo.save(task)).thenReturn(task);

        String result = service.createTask(task);

        verify(repo).save(task);

        assertEquals("1", result);
    }

    // ============================================================
    // 2. LIST TASKS - SUCCESS
    // ============================================================

    @Test
    void testListTask() {

        TaskRepository repo = mock(TaskRepository.class);
        TaskService service = new TaskService(repo);

        Pageable pageable = PageRequest.of(0, 10);

        Task task = new Task();
        task.setId("1");

        Page<Task> page =
                new PageImpl<>(List.of(task));

        when(repo.findAll(pageable))
                .thenReturn(page);

        Page<Task> result =
                service.listTasks(pageable);

        verify(repo).findAll(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(
                "1",
                result.getContent().get(0).getId()
        );
    }

    // ============================================================
    // 3. GET TASK - SUCCESS
    // ============================================================

    @Test
    void testGetTask() {

        TaskRepository repo = mock(TaskRepository.class);
        TaskService service = new TaskService(repo);

        Task task = new Task();
        task.setId("1");

        when(repo.findById("1"))
                .thenReturn(Optional.of(task));

        Task result =
                service.getTask("1");

        verify(repo).findById("1");

        assertEquals("1", result.getId());
    }

    // ============================================================
    // 4. GET TASK - NOT FOUND
    // ============================================================

    @Test
    void testGetTaskNotFound() {

        TaskRepository repo = mock(TaskRepository.class);
        TaskService service = new TaskService(repo);

        when(repo.findById("999"))
                .thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> service.getTask("999")
                );

        assertEquals(
                HttpStatus.NOT_FOUND,
                exception.getStatusCode()
        );

        verify(repo).findById("999");
    }

    // ============================================================
    // 5. GET TASK FALLBACK
    // ============================================================

    @Test
    void testGetTaskFallback() {

        TaskRepository repo = mock(TaskRepository.class);
        TaskService service = new TaskService(repo);

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> service.getTaskFallback(
                                "1",
                                new RuntimeException(
                                        "Database unavailable"
                                )
                        )
                );

        assertEquals(
                HttpStatus.SERVICE_UNAVAILABLE,
                exception.getStatusCode()
        );
    }

    // ============================================================
    // 6. UPDATE TASK - SUCCESS
    // ============================================================

    @Test
    void testUpdateTask() {

        TaskRepository repo = mock(TaskRepository.class);
        TaskService service = new TaskService(repo);

        Task task = new Task();
        task.setId("1");

        when(repo.save(task))
                .thenReturn(task);

        String result =
                service.updateTask(task);

        verify(repo).save(task);

        assertEquals(
                "Updated",
                result
        );
    }

    // ============================================================
    // 7. UPDATE TASK - OPTIMISTIC LOCKING FAILURE
    // ============================================================

    @Test
    void testUpdateTaskOptimisticLockingFailure() {

        TaskRepository repo = mock(TaskRepository.class);
        TaskService service = new TaskService(repo);

        Task task = new Task();
        task.setId("1");

        when(repo.save(task))
                .thenThrow(
                        new OptimisticLockingFailureException(
                                "Task was modified concurrently"
                        )
                );

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> service.updateTask(task)
                );

        assertEquals(
                HttpStatus.CONFLICT,
                exception.getStatusCode()
        );

        verify(repo).save(task);
    }

    // ============================================================
    // 8. DELETE TASK - SUCCESS
    // ============================================================

    @Test
    void testDeleteTask() {

        TaskRepository repo = mock(TaskRepository.class);
        TaskService service = new TaskService(repo);

        String id = "1";

        String result =
                service.deleteTask(id);

        verify(repo).deleteById(id);

        assertEquals(
                "Deleted",
                result
        );
    }

    // ============================================================
    // TEST RESULT WATCHER
    // ============================================================

    static class TestResultWatcher implements TestWatcher {

        @Override
        public void testSuccessful(
                ExtensionContext context) {

            RESULTS.add(
                    context.getDisplayName()
                    + " | PASS"
            );
        }

        @Override
        public void testFailed(
                ExtensionContext context,
                Throwable cause) {

            RESULTS.add(
                    context.getDisplayName()
                    + " | FAIL"
                    + " | "
                    + cause.getClass().getSimpleName()
                    + " | "
                    + cause.getMessage()
            );
        }

        @Override
        public void testAborted(
                ExtensionContext context,
                Throwable cause) {

            RESULTS.add(
                    context.getDisplayName()
                    + " | ABORTED"
            );
        }

        @Override
        public void testDisabled(
                ExtensionContext context,
                Optional<String> reason) {

            RESULTS.add(
                    context.getDisplayName()
                    + " | DISABLED"
            );
        }
    }

    // ============================================================
    // GENERATE TXT REPORT
    // ============================================================

    @AfterAll
    static void generateTestReport() {

        try {

            Path reportDirectory =
                    Path.of(
                            "target",
                            "test-reports"
                    );

            Files.createDirectories(
                    reportDirectory
            );

            Path reportFile =
                    reportDirectory.resolve(
                            "TaskServiceTestReport.txt"
                    );

            long passed =
                    RESULTS.stream()
                            .filter(r -> r.contains("| PASS"))
                            .count();

            long failed =
                    RESULTS.stream()
                            .filter(r -> r.contains("| FAIL"))
                            .count();

            long aborted =
                    RESULTS.stream()
                            .filter(r -> r.contains("| ABORTED"))
                            .count();

            long disabled =
                    RESULTS.stream()
                            .filter(r -> r.contains("| DISABLED"))
                            .count();

            int total = RESULTS.size();

            double passRate =
                    total == 0
                            ? 0.0
                            : (passed * 100.0) / total;

            StringBuilder report =
                    new StringBuilder();

            report.append(
                    "============================================\n"
            );

            report.append(
                    " TASK SERVICE TEST REPORT\n"
            );

            report.append(
                    "============================================\n\n"
            );

            report.append(
                    "TEST TYPE: UNIT TESTS - SERVICE LAYER\n\n"
            );

            report.append(
                    "TEST CASE STATUS\n"
            );

            report.append(
                    "--------------------------------------------\n"
            );

            for (String result : RESULTS) {

                report.append(result)
                        .append("\n");
            }

            report.append("\n");

            report.append(
                    "SUMMARY\n"
            );

            report.append(
                    "--------------------------------------------\n"
            );

            report.append(
                    "Tests Run : "
            ).append(total).append("\n");

            report.append(
                    "Passed : "
            ).append(passed).append("\n");

            report.append(
                    "Failed : "
            ).append(failed).append("\n");

            report.append(
                    "Aborted : "
            ).append(aborted).append("\n");

            report.append(
                    "Disabled : "
            ).append(disabled).append("\n");

            report.append(
                    "Pass Rate : "
            ).append(
                    String.format(
                            "%.2f%%",
                            passRate
                    )
            ).append("\n\n");

            report.append(
                    "REPORT STATUS\n"
            );

            report.append(
                    "--------------------------------------------\n"
            );

            if (failed == 0 && aborted == 0) {

                report.append(
                        "ALL EXECUTED TESTS PASSED\n"
                );

            } else {

                report.append(
                        "SOME TESTS REQUIRE ATTENTION\n"
                );
            }

            report.append("\n");

            report.append(
                    "Generated: "
            ).append(
                    LocalDateTime.now()
            ).append("\n");

            report.append(
                    "============================================\n"
            );

            Files.writeString(
                    reportFile,
                    report.toString()
            );

            System.out.println(
                    "TaskService test report generated: "
                    + reportFile.toAbsolutePath()
            );

        } catch (Exception e) {

            System.err.println(
                    "Unable to generate test report: "
                    + e.getMessage()
            );
        }
    }
}