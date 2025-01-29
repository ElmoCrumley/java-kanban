package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.SubTask;
import com.yandex.app.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TaskManager {
    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private Map<Integer, SubTask> subTasks = new HashMap<>();
    private int id = hashCode();

    // Получение списка всех задач.
    public ArrayList<Task> getTasksList() {
        ArrayList<Task> tasksList = new ArrayList<>();

        for (Task task : tasks.values()) {
            tasksList.add(task);
        }
        return tasksList;
    }

    public ArrayList<Epic> getEpicsList() {
        ArrayList<Epic> epicsList = new ArrayList<>();

        for (Epic epic : epics.values()) {
            epicsList.add(epic);
        }
        return epicsList;
    }

    public ArrayList<SubTask> getSubTasksList() {
        ArrayList<SubTask> subTasksList = new ArrayList<>();

        for (SubTask subTask : subTasks.values()) {
            subTasksList.add(subTask);
        }
        return subTasksList;
    }

    // Удаление всех задач.
    public void removeTasks() {
        tasks.clear();
    }

    public void removeEpics() {
            epics.clear();
            subTasks.clear();
    }

    public void removeSubTasks() {
            subTasks.clear();
            for (Epic epic : epics.values()) {
                epic.getSubTasksList().clear();
                epic.setStatus(Status.NEW.name());
            }
    }

    // Получение по идентификатору.
    public Task getTask(int id) {
        return tasks.get(id);
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public SubTask getSubTask(int id) {
        return subTasks.get(id);
    }

    // Создание.
    public void createTask (Task task) {
        int id = this.id++;

        this.id++;
        task.setId(id);
        tasks.put(id, task);
    }

    public void createEpic (Epic epic) {
        int id = this.id++;

        this.id++;
        epic.setId(id);
        epics.put(id, epic);
    }

    public void createSubTask (SubTask subTask, int epicsId) {
        int id = this.id++;

        this.id++;
        subTask.setId(id);
        subTasks.put(id, subTask);
        subTask.setEpicsId(epicsId);

        for (Epic epic : epics.values()) {
            if (epic.getId() == epicsId) {
                epic.setSubTaskToList(subTask);
                epic.setStatus("IN_PROGRESS");
            }
        }
    }

    // Обновление.
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateSubTask(SubTask subTask) {
        for (Epic epic : epics.values()) {
            if (epic.getId() == subTask.getEpicsId()) {
                epic.setStatus(Status.IN_PROGRESS.name());
                for (SubTask subTaskInList : epic.getSubTasksList()) {
                    if (subTaskInList.equals(subTask)) {
                        subTaskInList = subTask;
                        break;
                    }
                }
            }
        }

        subTasks.put(subTask.getId(), subTask);
    }

    // Удаление по идентификатору.
    public void removeTask(int id) {
        tasks.remove(id);
    }

    public void removeEpic(int id) {
        for (SubTask subTask : epics.get(id).getSubTasksList()) {
            subTasks.remove(subTask.getId());
        }
        epics.remove(id);
    }

    public void removeSubTask(int id) {
        for (Epic epic : epics.values()) {
            if (subTasks.get(id).getEpicsId() == epic.getId()) {
                epic.getSubTasksList().remove(subTasks.get(id));
            }
        }
        subTasks.remove(id);
    }

    // Получение списка всех подзадач определённого эпика.
    public ArrayList<SubTask> getEpicsSubTasksList(Epic epic) {
        return epic.getSubTasksList();
    }
}
