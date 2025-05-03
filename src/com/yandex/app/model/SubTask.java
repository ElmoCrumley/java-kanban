package com.yandex.app.model;

import com.yandex.app.service.Type;

public class SubTask extends Task {

    private int epicsId;

    public SubTask(String name, String description) {
        super(name, description);
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
