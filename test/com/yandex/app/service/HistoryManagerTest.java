package com.yandex.app.service;

import com.yandex.app.model.Task;
import com.yandex.app.model.TasksForTests;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.*;

public abstract class HistoryManagerTest {
    TaskManager taskManager;
    HistoryManager historyManager;
    TasksForTests tft;

    @Test
    public void testForAddTask() {
        historyManager.addTask(tft.task1);

        List<Task> history = historyManager.getHistory();

        assertNotNull(history);
        assertEquals(1, history.size());
        historyManager.addTask(tft.task2);
        history = historyManager.getHistory();
        assertNotNull(history);
        assertEquals(2, history.size());
    }

    @Test
    public void testForRemove() {
        historyManager.addTask(tft.task1);
        historyManager.addTask(tft.task2);
        historyManager.addTask(tft.task3);
        historyManager.remove(tft.task1.getId());

        final List<Task> history = historyManager.getHistory();
        int historySize = history.size();

        assertEquals(2, historySize);
    }

    @Test
    public void testForGetHistory() {
        List<Task> history;

        historyManager.addTask(tft.task1);
        history = historyManager.getHistory();
        assertEquals(1, history.size());
        historyManager.addTask(tft.task2);
        history = historyManager.getHistory();
        assertEquals(2, history.size());
        historyManager.addTask(tft.task3);
        history = historyManager.getHistory();
        assertEquals(3, history.size());
    }

    @Test
    public void testForClearAllHistory() {
        historyManager.addTask(tft.task1);
        historyManager.addTask(tft.task2);
        historyManager.addTask(tft.task3);
        historyManager.clearAllHistory();
        assertNull(historyManager.getHistory());
    }

    @Test
    public void testForHistoryIsEmpty() {
        assertTrue(historyManager.historyIsEmpty());
        historyManager.addTask(tft.task1);
        historyManager.addTask(tft.task2);
        historyManager.addTask(tft.task3);
        assertFalse(historyManager.historyIsEmpty());
        historyManager.clearAllHistory();
        assertTrue(historyManager.historyIsEmpty());
    }
}
