package com.yandex.app.model;

import com.yandex.app.service.Status;
import com.yandex.app.service.Type;

import java.util.ArrayList;

public class Epic extends Task {

    private final ArrayList<SubTask> subTasksList = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    @Override
    public Type getType() {
        return Type.valueOf(Type.EPIC.name());
    }

    public ArrayList<SubTask> getSubTasksList() {
        return subTasksList;
    }

    public void addSubTaskToList(SubTask subTask) {
        subTasksList.add(subTask);
    }

    public void recalculateStatus() {
        ArrayList<Object> statusCheckList = new ArrayList<>();
        for (SubTask subTask : subTasksList) {
            statusCheckList.add(subTask.getStatus());
        }
        if (statusCheckList.contains("IN_PROGRESS")) {
            setStatus(Status.valueOf(Status.IN_PROGRESS.name()));
        } else if (!statusCheckList.contains("IN_PROGRESS") && !statusCheckList.contains("NEW")) {
            setStatus(Status.valueOf(Status.DONE.name()));
        } else {
            setStatus(Status.valueOf(Status.NEW.name()));
        }
    }

}
