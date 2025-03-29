package com.yandex.app.model;

import com.yandex.app.service.Managers;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
class EpicTest {
    @Test
    void addNewTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");

        Managers.getDefault().createTask(task);

        int taskId = task.getId();

        Task savedTask = Managers.getDefault().getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = Managers.getDefault().getTasksList();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void add() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");

        Managers.getDefaultHistory().addTask(task);
        final List<Task> history = Managers.getDefaultHistory().getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }
}