package com.yandex.app.service;

import com.yandex.app.model.Task;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class InMemoryHistoryManagerTest {
    HistoryManager historyManager = Managers.getDefaultHistory();
    TaskManager taskManager = Managers.getDefault();
    Task task;
    Task task2;
    Task task3;

    @BeforeEach
    void beforeEach() {
        task = new Task("Test addNewTask", "Test addNewTask description");
        task2 = new Task("Test addNewTask", "Test addNewTask description");
        task3 = new Task("Test addNewTask", "Test addNewTask description");
        taskManager.createTask(task);
        taskManager.createTask(task2);
        taskManager.createTask(task3);
    }

    @AfterEach
    void afterEach() {
        historyManager.clearAllHistory();
        taskManager.clearAllTasks();
    }

    @Test
    void addTask() {
        historyManager.addTask(task);

        List<Task> history = historyManager.getHistory();

        assertNotNull(history);
        assertEquals(1, history.size());
        historyManager.addTask(task2);
        history = historyManager.getHistory();
        assertNotNull(history);
        assertEquals(2, history.size());
    }

    @Test
    void remove() {
        historyManager.addTask(task);
        historyManager.remove(task.getId());

        final List<Task> history = historyManager.getHistory();
        int historySize = history.size();

        assertEquals(0, historySize);
    }

    @Test
    void getHistory() {
        List<Task> history;

        historyManager.addTask(task);
        history = historyManager.getHistory();
        assertEquals(1, history.size());
        historyManager.addTask(task2);
        history = historyManager.getHistory();
        assertEquals(2, history.size());
        historyManager.addTask(task3);
        history = historyManager.getHistory();
        assertEquals(3, history.size());
    }
}