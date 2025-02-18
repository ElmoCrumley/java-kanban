package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.SubTask;
import com.yandex.app.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private Map<Integer, SubTask> subTasks = new HashMap<>();
    private int id = hashCode();
    public List<Task> historyList = new ArrayList<>();

    // Получение списка всех задач.
    @Override
    public ArrayList<Task> getTasksList() {
        ArrayList<Task> tasksList = new ArrayList<>();

        for (Task task : tasks.values()) {
            tasksList.add(task);
        }
        return tasksList;
    }

    @Override
    public ArrayList<Epic> getEpicsList() {
        ArrayList<Epic> epicsList = new ArrayList<>();

        for (Epic epic : epics.values()) {
            epicsList.add(epic);
        }
        return epicsList;
    }

    @Override
    public ArrayList<SubTask> getSubTasksList() {
        ArrayList<SubTask> subTasksList = new ArrayList<>();

        for (SubTask subTask : subTasks.values()) {
            subTasksList.add(subTask);
        }
        return subTasksList;
    }

    // Удаление всех задач.
    @Override
    public void removeTasks() {
        tasks.clear();
    }

    @Override
    public void removeEpics() {
            epics.clear();
            subTasks.clear();
    }

    @Override
    public void removeSubTasks() {
            subTasks.clear();
            for (Epic epic : epics.values()) {
                epic.getSubTasksList().clear();
                epic.setStatus(Status.NEW.name());
            }
    }

    // Получение по идентификатору.
    @Override
    public Task getTask(int id) {
        historyList.add(tasks.get(id));
        if (historyList.size() == 10) {
            historyList.remove(0);
        }
        return tasks.get(id);
    }

    @Override
    public Epic getEpic(int id) {
        historyList.add(epics.get(id));
        if (historyList.size() == 10) {
            historyList.remove(0);
        }
        return epics.get(id);
    }

    @Override
    public SubTask getSubTask(int id) {
        historyList.add(subTasks.get(id));
        if (historyList.size() == 10) {
            historyList.remove(0);
        }
        return subTasks.get(id);
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
    }

    @Override
    public void removeEpic(int id) {
        for (SubTask subTask : epics.get(id).getSubTasksList()) {
            subTasks.remove(subTask.getId());
        }
        epics.remove(id);
    }

    @Override
    public void removeSubTask(int id) {
        SubTask subTask = subTasks.get(id);
        Epic epic = epics.get(subTask.getEpicsId());

        epic.getSubTasksList().remove(subTask);
        subTasks.remove(id);
        epic.recalculateStatus();
    }

    @Override
    public List<Task> getHistory() {
        return historyList;
    }

    // Получение списка всех подзадач определённого эпика. (дополнительный метод)
    public ArrayList<SubTask> getEpicsSubTasksList(Epic epic) {
        return new ArrayList<>(List.copyOf(epic.getSubTasksList()));
    }
}
