package com.yandex.app.model;

import com.yandex.app.service.Status;
import com.yandex.app.service.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {

    private final ArrayList<SubTask> subTasksList = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void recalculateDuration() {
        if (super.getDuration() != null){
            super.setDuration(0);
            for (SubTask subTask : subTasksList) {
                if (subTask.getDuration() != null) {
                    super.getDuration().plus(subTask.getDuration());
                }
            }
        }
    }

    public void recalculateStartTime() {
        for (SubTask subTask : subTasksList) {
            if (subTask.getStartTime() != null) {
                LocalDateTime subTaskStartTime = subTask.getStartTime();
                if (subTaskStartTime.isBefore(super.getStartTime())) {
                    super.setStartTime(subTaskStartTime);
                }
            }
        }
    }

    public void recalculateEndTime() {
        for (SubTask subTask : subTasksList) {
            if (subTask.getStartTime() != null && subTask.getDuration() != null) {
                LocalDateTime subTaskEndTime = subTask.getStartTime().plus(subTask.getDuration());
                if (subTaskEndTime.isAfter(endTime)) {
                    endTime = subTaskEndTime;
                }
            }
        }
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
        if (!statusCheckList.contains(Status.IN_PROGRESS) && !statusCheckList.contains(Status.DONE)) {
            setStatus(Status.valueOf(Status.NEW.name()));
        } else if (!statusCheckList.contains(Status.IN_PROGRESS) && !statusCheckList.contains(Status.NEW)) {
            setStatus(Status.valueOf(Status.DONE.name()));
        } else {
            setStatus(Status.valueOf(Status.IN_PROGRESS.name()));
        }
    }
}
