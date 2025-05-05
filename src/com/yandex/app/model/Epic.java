package com.yandex.app.model;

import com.yandex.app.service.Status;
import com.yandex.app.service.Type;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {

    private final ArrayList<SubTask> subTasksList = new ArrayList<>();
    LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void recalculateDuration() {
        if (subTasksList != null) {
            super.duration = Duration.ZERO;
            for (SubTask subTask : subTasksList) {
                duration.plus(subTask.duration);
            }
        } else {
            super.duration = Duration.ZERO;
        }
    }

    public void recalculateStartTime() {
        super.startTime = subTasksList.getFirst().startTime;
    }

    public void recalculateEndTime() {
        endTime = subTasksList.getLast().startTime.plus(duration);
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
