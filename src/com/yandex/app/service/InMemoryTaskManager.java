package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.SubTask;
import com.yandex.app.model.Task;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private Map<Integer, SubTask> subTasks = new HashMap<>();
    private Collection<Task> allTasksWithDuration = new TreeSet<>(new DataComparator());
    private int id = hashCode();
    HistoryManager historyManager = Managers.getDefaultHistory();

    // Создание.
    @Override
    public void createTask(Task task) throws RuntimeException {
        if (isNotIntersectAny(task)) {
            this.id++;

            int id = this.id;

            task.setId(id);
            tasks.put(id, task);
            if (task.getDuration().toMinutes() != 0 && task.getStartTime() != null) {
                allTasksWithDuration.add(task);
            }
        } else {
            throw new RuntimeException();
        }
    }

    @Override
    public void createEpic(Epic epic) throws RuntimeException {
        if (isNotIntersectAny(epic)) {
            this.id++;

            int id = this.id;

            epic.setId(id);
            epics.put(id, epic);
            if (epic.getDuration().toMinutes() != 0 && epic.getStartTime() != null) {
                allTasksWithDuration.add(epic);
            }
        } else {
            throw new RuntimeException();
        }
    }

    @Override
    public void createSubTask(SubTask subTask, int epicsId) throws RuntimeException {
        if (isNotIntersectAny(subTask)) {
            this.id++;

            int id = this.id;

            subTask.setId(id);
            subTasks.put(id, subTask);
            if (subTask.getDuration().toMinutes() != 0 && subTask.getStartTime() != null) {
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
        } else {
            throw new RuntimeException();
        }
    }

    // Получение списка всех задач.
    @Override
    public ArrayList<Task> getTasksList() throws NotFoundException {
        ArrayList<Task> tasksList = new ArrayList<Task>(tasks.values());

        if (!tasksList.isEmpty()) {
            return tasksList;
        } else {
            throw new NotFoundException();
        }
    }

    @Override
    public ArrayList<Epic> getEpicsList() throws NotFoundException {
        ArrayList<Epic> epicsList = new ArrayList<Epic>(epics.values());

        if (!epicsList.isEmpty()) {
            return epicsList;
        } else {
            throw new NotFoundException();
        }
    }

    @Override
    public ArrayList<SubTask> getSubTasksList() throws NotFoundException {
        ArrayList<SubTask> subtasksList = new ArrayList<SubTask>(subTasks.values());

        if (!subtasksList.isEmpty()) {
            return subtasksList;
        } else {
            throw new NotFoundException();
        }
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
    public Task getTask(int id) throws NotFoundException {
        Task task = tasks.get(id);

        if (task != null) {
            historyManager.addTask(task);
            return task;
        } else {
            throw new NotFoundException();
        }
    }

    @Override
    public Epic getEpic(int id) throws NotFoundException {
        Epic epic = epics.get(id);

        if (epic != null) {
            historyManager.addTask(epic);
            return epic;
        } else {
            throw new NotFoundException();
        }
    }

    @Override
    public SubTask getSubTask(int id) throws NotFoundException {
        SubTask subTask = subTasks.get(id);

        if (subTask != null) {
            historyManager.addTask(subTask);
            return subTask;
        } else {
            throw new NotFoundException();
        }
    }

    // Обновление.
    @Override
    public void updateTask(Task task) throws RuntimeException {
        if (isNotIntersectAny(task)) {
            tasks.put(task.getId(), task);
        } else {
            throw new RuntimeException();
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) throws RuntimeException {
        if (isNotIntersectAny(subTask)) {
            int subTaskId = subTask.getId();
            if (subTask.getEpicsId() != 0) {
                Epic epic = epics.get(subTask.getEpicsId());

                epic.getSubTasksList().remove(subTasks.get(subTaskId));
                epic.addSubTaskToList(subTask);
                epic.recalculateStatus();
                epic.recalculateDuration();
                epic.recalculateStartTime();
                epic.recalculateEndTime();
            }
            subTasks.put(subTaskId, subTask);
        } else {
            throw new RuntimeException();
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
        if (subTask.getEpicsId() != 0) {
            Epic epic = epics.get(subTask.getEpicsId());

            epic.getSubTasksList().remove(subTask);
            epic.recalculateStatus();
            epic.recalculateDuration();
            epic.recalculateStartTime();
            epic.recalculateEndTime();
        }
        historyManager.remove(id);
        subTasks.remove(id);
    }

    @Override
    public void clearAllTasks() {
        removeTasks();
        removeEpics();
    }

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    public Map<Integer, Task> getTasks() {
        return tasks;
    }

    public Map<Integer, Epic> getEpics() {
        return epics;
    }

    public Map<Integer, SubTask> getSubTasks() {
        return subTasks;
    }

    // Получение списка всех подзадач определённого эпика. (дополнительный метод)
    public static ArrayList<SubTask> getEpicsSubTasksList(Epic epic) throws NotFoundException {
        try {
            return new ArrayList<>(epic.getSubTasksList());
        } catch (NotFoundException nfe) {
            throw new NotFoundException();
        }
    }

    public ArrayList<Task> getPrioritizedTasks() {
        return new ArrayList<>(allTasksWithDuration);
    }

    public boolean isNotIntersectAny(Task t2) {
        if (!allTasksWithDuration.isEmpty()) {
            return (allTasksWithDuration.stream().filter(task -> task.isIntersect(t2)).count() == 0);
        } else {
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
