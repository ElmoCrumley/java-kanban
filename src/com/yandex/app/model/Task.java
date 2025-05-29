package com.yandex.app.model;

import com.yandex.app.service.Status;
import com.yandex.app.service.Type;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private int id;
    private Status status;
    private Duration duration; // Продолжительность задачи в минутах
    private LocalDateTime startTime;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = Status.valueOf(Status.NEW.name());
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    public void setDuration(int minutes) {
        this.duration = Duration.ofMinutes(minutes);
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime.plusMinutes(duration.toMinutes());
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public Object getStatus() {
        return status;
    }

    public Type getType() {
        return Type.valueOf(Type.TASK.name());
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isIntersect(Task t2) {
        if (startTime.isBefore(t2.getStartTime())) {
            return t2.getStartTime().isBefore(getEndTime());
        } else if (startTime.isAfter(t2.getStartTime())) {
            return !t2.getStartTime().isBefore(getEndTime());
        } else {
            return true;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Task otherTask = (Task) obj;
        return Objects.equals(name, otherTask.name) && // проверяем все поля
                Objects.equals(description, otherTask.description) && // нужно логическое «и»
                (id == otherTask.id) &&
                Objects.equals(status, otherTask.status); // примитивы сравниваем через ==
    }

    @Override
    public int hashCode() {
        int hash = 17;
        if (name != null) {
            hash = hash + name.hashCode();
        }
        hash = hash * 31;

        if (description != null) {
            hash = hash + description.hashCode();
        }
        return hash;
    }
}

