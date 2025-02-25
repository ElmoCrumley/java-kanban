package com.yandex.app.service;

import com.yandex.app.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    public List<Task> historyList = new ArrayList<>();
    public static final int historyLimit = 10;

    @Override
    public void addTask(Task task) {
        if (task != null) {
            historyList.add(task);
        }
        if (historyList.size() == historyLimit) {
            historyList.remove(0);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyList;
    }
}
