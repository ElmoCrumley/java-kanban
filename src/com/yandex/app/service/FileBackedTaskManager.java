package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.SubTask;
import com.yandex.app.model.Task;
import java.io.File;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    File autoSave;

    public FileBackedTaskManager(File autoSave) {
        this.autoSave = autoSave;
    }

//    @Override
//    public void addSubtask(Subtask subtask) {
//        super.addSubtask(subtask);
//        save();
//    }

    // Запись в файл.
    public void save() {

    }

    public String toString(Task task) {
        return task.getId() + ","
                + task.getType() + ","
                + task.getName() + ","
                + task.getStatus() + ","
                + task.getDescription();
    }

    public String toString(SubTask subTask) {
        return subTask.getId() + ","
                + subTask.getName() + ","
                + subTask.getStatus() + ","
                + subTask.getDescription() + ","
                + subTask.getEpicsId();
    }

    // Восстановление данных из файла.
    static FileBackedTaskManager loadFromFile(File file) {
        // Построчно создавать задачи. Для эпиков создать списки подзадач
    }

    public Task fromString(String value) {
        String[] split = value.split(","); // id,type,name,status,description,epic
        switch (split[1]) {
            case "TASK":
                Task task = new Task(split[2], split[4]);
                task.setId(Integer.parseInt(split[0]));
                task.setStatus(Status.valueOf(split[3]));
                return task;
            case "EPIC":
                Epic epic = new Epic(split[2], split[4]);
                epic.setId(Integer.parseInt(split[0]));
                epic.setStatus(Status.valueOf(split[3]));
                return epic;
            case "SUBTASK":
                SubTask subTask = new SubTask(split[2], split[4]);
                subTask.setId(Integer.parseInt(split[0]));
                subTask.setStatus(Status.valueOf(split[3]));
                subTask.setEpicsId(Integer.parseInt(split[5]));
                return subTask;
            default:
                return null;
        }
    }
}
