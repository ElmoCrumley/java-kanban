package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.SubTask;
import com.yandex.app.model.Task;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private Map<Integer, SubTask> subTasks = new HashMap<>();
    private Collection<Task> allTasksWithDuration = new TreeSet<>(new DataComparator());
    private int id = hashCode();
    HistoryManager historyManager = Managers.getDefaultHistory();

    public Map<Integer, Task> getTasks() {
        return tasks;
    }

    public Map<Integer, Epic> getEpics() {
        return epics;
    }

    public Map<Integer, SubTask> getSubTasks() {
        return subTasks;
    }

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
            historyManager.remove(task.getId());
        }
        tasks.clear();
    }

    @Override
    public void removeEpics() {
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
        }
        epics.clear();
        for (SubTask subTask : subTasks.values()) {
            historyManager.remove(subTask.getId());
        }
        subTasks.clear();
    }

    @Override
    public void removeSubTasks() {
        for (SubTask subTask : subTasks.values()) {
            historyManager.remove(subTask.getId());
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

        historyManager.addTask(task);
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);

        historyManager.addTask(epic);
        return epic;
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask subTask = subTasks.get(id);

        historyManager.addTask(subTask);
        return subTask;
    }

    // Создание.
    @Override
    public void createTask(Task task) {
        this.id++;

        int id = this.id;

        task.setId(id);
        tasks.put(id, task);
        if (task.getDuration() != null || task.getStartTime() != null) {
            allTasksWithDuration.add(task);
        }
    }

    @Override
    public void createEpic(Epic epic) {
        this.id++;

        int id = this.id;

        epic.setId(id);
        epics.put(id, epic);
        if (epic.getDuration() != null || epic.getStartTime() != null) {
            allTasksWithDuration.add(epic);
        }
    }

    @Override
    public void createSubTask(SubTask subTask, int epicsId) {
        this.id++;

        int id = this.id;

        subTask.setId(id);
        subTasks.put(id, subTask);
        if (subTask.getDuration() != null || subTask.getStartTime() != null) {
            allTasksWithDuration.add(subTask);
        }
        subTask.setEpicsId(epicsId);

        for (Epic epic : epics.values()) {
            if (epic.getId() == epicsId) {
                epic.addSubTaskToList(subTask);
                epic.recalculateDuration();
                epic.recalculateEndTime();
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
        epic.recalculateDuration();
        epic.recalculateStartTime();
        epic.recalculateEndTime();
        subTasks.put(subTaskId, subTask);
    }

    // Удаление по идентификатору.
    @Override
    public void removeTask(int id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeEpic(int id) {
        for (SubTask subTask : epics.get(id).getSubTasksList()) {
            historyManager.remove(subTask.getId());
            subTasks.remove(subTask.getId());
        }
        historyManager.remove(id);
        epics.remove(id);
    }

    @Override
    public void removeSubTask(int id) {
        SubTask subTask = subTasks.get(id);
        Epic epic = epics.get(subTask.getEpicsId());

        epic.getSubTasksList().remove(subTask);
        historyManager.remove(id);
        subTasks.remove(id);
        epic.recalculateStatus();
        epic.recalculateDuration();
        epic.recalculateStartTime();
        epic.recalculateEndTime();
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

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    public ArrayList<Task> getPrioritizedTasks() {
        return new ArrayList<>(allTasksWithDuration);
    }

    public boolean isIntersectAny(Task t2) {
        return !(allTasksWithDuration.stream().filter(task -> task.isIntersect(t2)).count() == 0);
    }

    public static class DataComparator implements Comparator<Task> {
        @Override
        public int compare(Task o1, Task o2) {
            if (o1.getStartTime().isBefore(o2.getStartTime())) {
                return -1;
            } else if (o1.getStartTime().isAfter(o2.getStartTime())) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
