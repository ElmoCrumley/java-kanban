package com.yandex.app.model;

import java.util.ArrayList;

public class Epic extends Task {

    private final ArrayList<String> subTasksList = new ArrayList<>();
    public int backlogLevel = 0;

    public Epic(String name, String description) {
        super(name, description);
    }

    public ArrayList<String> getEpicsSubTasksList() {
        return subTasksList;
    }

    public void setSubtaskToList(String subTaskName) {
        subTasksList.add(subTaskName);
    }
}
