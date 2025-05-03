package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.SubTask;
import com.yandex.app.model.Task;
import java.io.File;

public class Managers {
    public static TaskManager getDefault(File file) {
        return FileBackedTaskManager.loadFromFile(file);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getTasksList()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Epic epic : manager.getEpicsList()) {
            System.out.println(epic);

            for (Task task : epic.getSubTasksList()) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (SubTask subtask : manager.getSubTasksList()) {
            System.out.println(subtask);
        }

        System.out.println("История:");

    }
}