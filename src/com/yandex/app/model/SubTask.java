package com.yandex.app.model;

import com.yandex.app.service.Type;
import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {

    private int epicsId;

    public SubTask(String name, String description, Duration duration, LocalDateTime startTime, int epicsId) {
        super(name, description, duration, startTime);
        this.epicsId = epicsId;
    }

    @Override
    public Type getType() {
        return Type.valueOf(Type.SUBTASK.name());
    }

    public int getEpicsId() {
        return epicsId;
    }

    public void setEpicsId(int epicsId) {
        this.epicsId = epicsId;
    }
}