package com.yandex.app.model;

import com.yandex.app.service.HistoryManager;
import com.yandex.app.service.Managers;
import com.yandex.app.service.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
class EpicTest {
    Task task;
    Task task2;
    Task task3;
    File log;

    TaskManager taskManager;
    HistoryManager historyManager;

    @BeforeEach
    void beforeEach() throws IOException {
        try {
            log = File.createTempFile("myTempFile", ".txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        taskManager = Managers.getDefault(log.getAbsoluteFile());
        historyManager = taskManager.getHistoryManager();
    }

    @AfterEach
    void afterEach() {
        taskManager.clearAllTasks();
        historyManager.clearAllHistory();
    }

    @Test
    void addNewTaskTest() {
        // Первая задача
        task = new Task("Test addNewTask", "Test addNewTask description");

        taskManager.createTask(task);

        int taskId = task.getId();

        Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        List<Task> tasks = taskManager.getTasksList();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");

        // Вторая задача
        task2 = new Task("Test addNewTask", "Test addNewTask description");

        taskManager.createTask(task2);

        int taskId2 = task2.getId();

        Task savedTask2 = taskManager.getTask(taskId2);

        assertNotNull(savedTask2, "Задача не найдена.");
        assertEquals(task2, savedTask2, "Задачи не совпадают.");

        tasks = taskManager.getTasksList();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(2, tasks.size(), "Неверное количество задач.");


        // Третья задача
        task3 = new Task("Test addNewTask", "Test addNewTask description");

        taskManager.createTask(task3);

        int taskId3 = task3.getId();

        Task savedTask3 = taskManager.getTask(taskId3);

        assertNotNull(savedTask3, "Задача не найдена.");
        assertEquals(task3, savedTask3, "Задачи не совпадают.");

        tasks = taskManager.getTasksList();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(3, tasks.size(), "Неверное количество задач.");
    }
}