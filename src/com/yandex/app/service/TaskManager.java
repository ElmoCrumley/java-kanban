package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.SubTask;
import com.yandex.app.model.Task;

import java.util.ArrayList;

public interface TaskManager {
    // Создание.
    void createTask(Task task) throws RuntimeException;

    void createEpic(Epic epic) throws RuntimeException;

    void createSubTask(SubTask subTask, int epicsId) throws RuntimeException;

    // Получение списка всех задач.
    ArrayList<Task> getTasksList() throws NotFoundException;

    ArrayList<Epic> getEpicsList() throws NotFoundException;

    ArrayList<SubTask> getSubTasksList() throws NotFoundException;

    // Удаление всех задач.
    void removeTasks();

    void removeEpics();

    void removeSubTasks();

    // Получение по идентификатору.
    Task getTask(int id) throws NotFoundException;

    Epic getEpic(int id) throws NotFoundException;

    SubTask getSubTask(int id) throws NotFoundException;

    // Обновление.
    void updateTask(Task task) throws RuntimeException;

    void updateSubTask(SubTask subTask) throws RuntimeException;

    // Удаление по идентификатору.
    void removeTask(int id);

    void removeEpic(int id);

    void removeSubTask(int id);

    void clearAllTasks();

    HistoryManager getHistoryManager();

    ArrayList<Task> getPrioritizedTasks();
}
