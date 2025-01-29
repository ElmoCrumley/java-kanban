package com.yandex.app.model;

import java.util.ArrayList;

public class Epic extends Task {

    private final ArrayList<SubTask> subTasksList = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public ArrayList<SubTask> getEpicsSubTasksList() {
        return subTasksList;
    }

    public void setSubTaskToList(SubTask subTask) {
        subTasksList.add(subTask);
    }
}
