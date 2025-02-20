package com.yandex.app.service;

import com.yandex.app.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    public List<Task> historyList = new ArrayList<>();

    @Override
    public void add(Task task) {

    }

    @Override
    public List<Task> getHistory() {
        return historyList;
    }
}
