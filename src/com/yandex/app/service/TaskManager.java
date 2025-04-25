package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.SubTask;
import com.yandex.app.model.Task;

import java.util.ArrayList;

public interface TaskManager {
    // Получение списка всех задач.
    ArrayList<Task> getTasksList();

    ArrayList<Epic> getEpicsList();

    ArrayList<SubTask> getSubTasksList();

    // Удаление всех задач.
    void removeTasks();

    void removeEpics();

    void removeSubTasks();

    // Получение по идентификатору.
    Task getTask(int id);

    Epic getEpic(int id);

    SubTask getSubTask(int id);

    // Создание.
    void createTask(Task task);

    void createEpic(Epic epic);

    void createSubTask(SubTask subTask, int epicsId);

    // Обновление.
    void updateTask(Task task);

    void updateSubTask(SubTask subTask);

    // Удаление по идентификатору.
    void removeTask(int id);

    void removeEpic(int id);

    void removeSubTask(int id);

    void clearAllTasks();

    HistoryManager getHistoryManager();

    TaskManager getTaskManager();
}
