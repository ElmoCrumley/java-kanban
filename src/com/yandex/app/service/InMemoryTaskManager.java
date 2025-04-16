package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.SubTask;
import com.yandex.app.model.Task;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private Map<Integer, SubTask> subTasks = new HashMap<>();
    private int id = hashCode();
    // HistoryManager historyManager = Managers.getDefaultHistory();

    // Получение списка всех задач.
    @Override
    public ArrayList<Task> getTasksList() {
        return new ArrayList<Task>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getEpicsList() {
        return new ArrayList<Epic>(epics.values());
    }

    @Override
    public ArrayList<SubTask> getSubTasksList() {
        return new ArrayList<SubTask>(subTasks.values());
    }

    // Удаление всех задач.
    @Override
    public void removeTasks() {
        for (Task task : tasks.values()) {
            Managers.getDefaultHistory().remove(task.getId());
        }
        tasks.clear();
    }

    @Override
    public void removeEpics() {
        for (Epic epic : epics.values()) {
            Managers.getDefaultHistory().remove(epic.getId());
        }
        epics.clear();
        for (SubTask subTask : subTasks.values()) {
            Managers.getDefaultHistory().remove(subTask.getId());
        }
        subTasks.clear();
    }

    @Override
    public void removeSubTasks() {
        for (SubTask subTask : subTasks.values()) {
            Managers.getDefaultHistory().remove(subTask.getId());
        }
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubTasksList().clear();
            epic.setStatus(Status.valueOf(Status.NEW.name()));
        }
    }

    // Получение по идентификатору.
    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);

        Managers.getDefaultHistory().addTask(task);
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);

        Managers.getDefaultHistory().addTask(epic);
        return epic;
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask subTask = subTasks.get(id);

        Managers.getDefaultHistory().addTask(subTask);
        return subTask;
    }

    // Создание.
    @Override
    public void createTask(Task task) {
        this.id++;

        int id = this.id;

        task.setId(id);
        tasks.put(id, task);
    }

    @Override
    public void createEpic(Epic epic) {
        this.id++;

        int id = this.id;

        epic.setId(id);
        epics.put(id, epic);
    }

    @Override
    public void createSubTask(SubTask subTask, int epicsId) {
        this.id++;

        int id = this.id;

        subTask.setId(id);
        subTasks.put(id, subTask);
        subTask.setEpicsId(epicsId);

        for (Epic epic : epics.values()) {
            if (epic.getId() == epicsId) {
                epic.addSubTaskToList(subTask);
            }
        }
    }

    // Обновление.
    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        int subTaskId = subTask.getId();
        Epic epic = epics.get(subTask.getEpicsId());

        epic.getSubTasksList().remove(subTasks.get(subTaskId));
        epic.addSubTaskToList(subTask);
        epic.recalculateStatus();
        subTasks.put(subTaskId, subTask);
    }

    // Удаление по идентификатору.
    @Override
    public void removeTask(int id) {
        tasks.remove(id);
        Managers.getDefaultHistory().remove(id);
    }

    @Override
    public void removeEpic(int id) {
        for (SubTask subTask : epics.get(id).getSubTasksList()) {
            Managers.getDefaultHistory().remove(subTask.getId());
            subTasks.remove(subTask.getId());
        }
        Managers.getDefaultHistory().remove(id);
        epics.remove(id);
    }

    @Override
    public void removeSubTask(int id) {
        SubTask subTask = subTasks.get(id);
        Epic epic = epics.get(subTask.getEpicsId());

        epic.getSubTasksList().remove(subTask);
        Managers.getDefaultHistory().remove(id);
        subTasks.remove(id);
        epic.recalculateStatus();
    }

    @Override
    public void clearAllTasks() {
        removeTasks();
        removeEpics();
    }

    // Получение списка всех подзадач определённого эпика. (дополнительный метод)
    public static ArrayList<SubTask> getEpicsSubTasksList(Epic epic) {
        return new ArrayList<>(epic.getSubTasksList());
    }
}
