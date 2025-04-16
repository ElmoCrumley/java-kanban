package com.yandex.app.model;

import com.yandex.app.service.Managers;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
class EpicTest {
    @Test
    void addNewTaskTest() {
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
        Managers.getDefault().clearAllTasks();
        Managers.getDefaultHistory().clearAllHistory();
    }

    @Test
    void add() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");

        Managers.getDefaultHistory().addTask(task);
        final List<Task> history = Managers.getDefaultHistory().getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "Размер листа больше 1");
        Managers.getDefault().clearAllTasks();
        Managers.getDefaultHistory().clearAllHistory();
    }

    @Test
    void addLast() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        Task task2 = new Task("Test addNewTask 2", "Test addNewTask description 2");

        Managers.getDefault().createTask(task);
        Managers.getDefault().createTask(task2);

        int taskId = task.getId();
        int taskId2 = task2.getId();

        Task savedTask = Managers.getDefault().getTask(taskId); // я не понимаю почему у меня Managers.getDefaultHistory(); :null
        Task savedTask2 = Managers.getDefault().getTask(taskId2);
        final List<Task> history = Managers.getDefaultHistory().getHistory();

        assertNotNull(history, "История пустая.");
        assertEquals(2, history.size(), "Количество элементов не соответствует ожидаемому.");
        Managers.getDefaultHistory().remove(taskId);
        Managers.getDefaultHistory().remove(taskId2);
        assertFalse(history.contains(taskId), "Задача не удалена.");
        assertFalse(history.contains(taskId2), "Задача 2 не удалена.");
        Managers.getDefault().clearAllTasks();
        Managers.getDefaultHistory().clearAllHistory();
    }
}