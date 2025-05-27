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
        if (!historyManager.historyIsEmpty()) {
            historyManager.getHistory().removeAll(tasks.values());
        }
        tasks.clear();
    }

    @Override
    public void removeEpics() {
        if (!historyManager.historyIsEmpty()) {
            historyManager.getHistory().removeAll(epics.values());
            historyManager.getHistory().removeAll(subTasks.values());
        }
        epics.clear();
        subTasks.clear();
    }

    @Override
    public void removeSubTasks() {
        if (!historyManager.historyIsEmpty()) {
            historyManager.getHistory().removeAll(subTasks.values());
        }
        subTasks.clear();
        epics.values().forEach(epic -> {
            epic.getSubTasksList().clear();
            epic.setStatus(Status.valueOf(Status.NEW.name()));
        });
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
        if (isNotIntersectAny(task)) {
            this.id++;

            int id = this.id;

            task.setId(id);
            tasks.put(id, task);
            if (task.getDuration() != null || task.getStartTime() != null) {
                allTasksWithDuration.add(task);
            }
        }
    }

    @Override
    public void createEpic(Epic epic) {
        if (isNotIntersectAny(epic)) {
            this.id++;

            int id = this.id;

            epic.setId(id);
            epics.put(id, epic);
            if (epic.getDuration() != null || epic.getStartTime() != null) {
                allTasksWithDuration.add(epic);
            }
        }
    }

    @Override
    public void createSubTask(SubTask subTask, int epicsId) {
        if (isNotIntersectAny(subTask)) {
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
    }

    // Обновление.
    @Override
    public void updateTask(Task task) {
        if (isNotIntersectAny(task)) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (isNotIntersectAny(subTask)) {
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

    public boolean isNotIntersectAny(Task t2) {
        if (!allTasksWithDuration.isEmpty()) {
            return (allTasksWithDuration.stream().filter(task -> task.isIntersect(t2)).count() == 0);
        } else{
            return true;
        }
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
