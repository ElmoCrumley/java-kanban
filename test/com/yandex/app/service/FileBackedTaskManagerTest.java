package com.yandex.app.service;

import com.yandex.app.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.*;
import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    Task task;
    Task task2;
    Task task3;
    File log;
    TaskManager fileBackedtaskManager;

    @BeforeEach
    void beforeEach() {
        task = new Task("Test addNewTask", "Test addNewTask description");
        task2 = new Task("Test addNewTask", "Test addNewTask description");
        task3 = new Task("Test addNewTask", "Test addNewTask description");

        try {
            log = File.createTempFile("myTempFile", ".txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        fileBackedtaskManager = Managers.getDefaultSave(log.getAbsoluteFile());
    }

    @Test
    void createTask() throws IOException {
        assertNotNull(log);


    }
}