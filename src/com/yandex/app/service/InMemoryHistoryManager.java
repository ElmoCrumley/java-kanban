package com.yandex.app.service;

import com.yandex.app.model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    public Map<Integer, Task> historyList = new LinkedHashMap<>();

    @Override
    public void addTask(Task task) {
        if (task != null) {
            historyList.put(task.getId(), task);
        }
    }

    @Override
    public void remove(int id) {
        historyList.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>((Collection) Map.copyOf(historyList));
    }
}
