package com.yandex.app.service;

import com.yandex.app.model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    Task task;
    Task task2;
    Task task3;
    static File autoSave;
    TaskManager fileBackedtaskManager;
    TaskManager taskManager;

    @BeforeEach
    void beforeEach() {
        try {
            autoSave = new File(String.valueOf(Files.createTempFile("autoSave", ".txt")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        fileBackedtaskManager = new FileBackedTaskManager(autoSave);
        taskManager = fileBackedtaskManager.getTaskManager();
        task = new Task("Test addNewTask", "Test addNewTask description");
        task2 = new Task("Test addNewTask", "Test addNewTask description");
        task3 = new Task("Test addNewTask", "Test addNewTask description");
    }

    @AfterEach
    void afterEach() {
        taskManager.clearAllTasks();
        taskManager.getHistoryManager().clearAllHistory();
    }

    @Test
    void createTask() {
        assertNotNull(autoSave);

        fileBackedtaskManager.createTask(task);

    }
}