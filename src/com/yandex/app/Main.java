package com.yandex.app;

import com.yandex.app.model.Epic;
import com.yandex.app.model.SubTask;
import com.yandex.app.model.Task;
import com.yandex.app.service.Managers;

public class Main {

    public static void main(String[] args) {
        //Создал задачи различных типов.
        Task task1 = new Task("Задача 1.", "Описание задачи.");
        Managers.getDefault().createTask(task1);

        Task task2 = new Task("Задача 2.", "Описание задачи.");
        Managers.getDefault().createTask(task2);

        Epic epic1 = new Epic("Эпик 1.", "Описание эпика.");
        SubTask subTask1point1 = new SubTask("Подзадача 1 эпика 1.", "Описание подзадачи.");
        SubTask subTask1point2 = new SubTask("Подзадача 2 эпика 1.", "Описание подзадачи.");
        Managers.getDefault().createEpic(epic1);
        Managers.getDefault().createSubTask(subTask1point1, epic1.getId());
        Managers.getDefault().createSubTask(subTask1point2, epic1.getId());

        Epic epic2 = new Epic("Эпик 2.", "Описание эпика.");
        SubTask subTask2 = new SubTask("Подзадача 1 эпика 2.", "Описание подзадачи.");
        Managers.getDefault().createEpic(epic2);
        Managers.getDefault().createSubTask(subTask2, epic2.getId());

        // Проверка получения метода
        Managers.getDefault().getTask(task1.getId());
        Managers.getDefault().getEpic(epic1.getId());
        Managers.getDefault().getSubTask(subTask1point1.getId());

        Managers.printAllTasks(Managers.getDefault());
    }
}
