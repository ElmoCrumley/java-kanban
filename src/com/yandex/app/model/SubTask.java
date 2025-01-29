package com.yandex.app.model;

public class SubTask extends Task {

    private int epicsId;

    public SubTask(String name, String description) {
        super(name, description);
    }

    public int getEpicsId() {
        return epicsId;
    }

    public void setEpicsId(int epicsId) {
        this.epicsId = epicsId;
    }
}
