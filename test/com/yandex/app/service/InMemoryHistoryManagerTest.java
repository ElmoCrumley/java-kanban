package com.yandex.app.service;

import com.yandex.app.model.Task;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    @Test
    void addTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");

        Managers.getDefaultHistory().addTask(task);

        List<Task> history = Managers.getDefaultHistory().getHistory();

        assertNotNull(history);
        assertEquals(1, history.size());
        Managers.getDefaultHistory().clearAllHistory();
    }

    @Test
    void remove() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");

        Managers.getDefaultHistory().addTask(task);
        Managers.getDefaultHistory().remove(task.getId());

        final List<Task> history = Managers.getDefaultHistory().getHistory();
        int historySize = history.size();

        assertEquals(0, historySize);
        Managers.getDefaultHistory().clearAllHistory();
    }

    @Test
    void getHistory() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        Task task2 = new Task("Test addNewTask", "Test addNewTask description");
        Task task3 = new Task("Test addNewTask", "Test addNewTask description");
        List<Task> history;

        Managers.getDefaultHistory().addTask(task);
        history = Managers.getDefaultHistory().getHistory();
        assertEquals(1, history.size());
        Managers.getDefaultHistory().addTask(task2);
        history = Managers.getDefaultHistory().getHistory();
        assertEquals(2, history.size());
        Managers.getDefaultHistory().addTask(task3);
        history = Managers.getDefaultHistory().getHistory();
        assertEquals(3, history.size());
        Managers.getDefaultHistory().clearAllHistory();
    }
}