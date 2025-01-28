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
                epic.getEpicsSubTasksList().clear();
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
        int id = task.hashCode();

        task.setId(id);
        tasks.put(id, task);
    }

    public void createEpic (Epic epic) {
        int id = epic.hashCode();

        epic.setId(id);
        epics.put(id, epic);
    }

    public void createSubTask (SubTask subTask, String epicsName) {
        int id = subTask.hashCode();

        subTask.setId(id);
        subTasks.put(id, subTask);
        subTask.setEpicsName(epicsName);

        for (Epic epic : epics.values()) {
            if (epic.getName().equals(epicsName)) {
                epic.setSubtaskToList(subTask.getName());
                epic.backlogLevel++;
            }
        }
    }

    // Обновление.
    public void updateTask(Task task) {
        if (task.getStatus().equals("NEW")) {
            task.setStatus(Status.IN_PROGRESS.name());
            tasks.put(task.getId(), task);
        } else if (task.getStatus().equals("IN_PROGRESS")) {
            task.setStatus(Status.DONE.name());
            tasks.put(task.getId(), task);
        }
    }

    public void updateSubTask(SubTask subTask) {
        if (subTask.getStatus().equals("NEW")) {
            subTask.setStatus(Status.IN_PROGRESS.name());
            subTasks.put(subTask.getId(), subTask);
            for (Epic epic : epics.values()) {
                if (epic.getName().equals(subTask.getEpicsName())) {
                    epic.setStatus(Status.IN_PROGRESS.name());
                    epics.put(epic.getId(), epic);
                }
            }
        } else if (subTask.getStatus().equals("IN_PROGRESS")) {
            subTask.setStatus(Status.DONE.name());
            subTasks.put(subTask.getId(), subTask);
            for (Epic epic : epics.values()) {
                if (subTask.getEpicsName().equals(epic.getName())) {
                    epic.backlogLevel--;
                    if (epic.backlogLevel == 0) {
                        epic.setStatus(Status.DONE.name());
                        epics.put(epic.getId(), epic);
                    }
                }
            }
        }
    }

    // Удаление по идентификатору.
    public void removeTask(int id) {
        tasks.remove(id);
    }

    public void removeEpic(int id) {
        for (String epicsSubTaskName : epics.get(id).getEpicsSubTasksList()) {
            for (SubTask subTask : subTasks.values()) {
                if (subTask.getName().equals(epicsSubTaskName)) {
                    subTasks.remove(subTask.getId());
                    break;
                }
            }
        }
        epics.remove(id);
    }

    public void removeSubTask(int id) {
        for (Epic epic : epics.values()) {
            if (subTasks.get(id).getEpicsName().equals(epic.getName())) {
                epic.getEpicsSubTasksList().remove(subTasks.get(id).getEpicsName());
            }
        }
        subTasks.remove(id);
    }

    // Получение списка всех подзадач определённого эпика.
    public ArrayList<String> getEpicsSubTasksList(Epic epic) {
        return epic.getEpicsSubTasksList();
    }
}
