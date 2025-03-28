package com.yandex.app.service;

import com.yandex.app.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    public List<Task> historyList = new ArrayList<>();

    @Override
    public void addTask(Task task) {
        if (task != null) {
            historyList.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(List.copyOf(historyList));
    }
}
