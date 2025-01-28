package com.yandex.app.model;

public class SubTask extends Task {

    private String epicsName;

    public SubTask(String name, String description) {
        super(name, description);
    }

    public String getEpicsName() {
        return epicsName;
    }

    public void setEpicsName(String epicsName) {
        this.epicsName = epicsName;
    }
}
