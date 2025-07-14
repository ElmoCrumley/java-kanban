package com.yandex.app.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class TasksForTests {
    public Task task1 = new Task("Test task1", "Test task1 description", Duration.ofMinutes(0), LocalDateTime.now());
    public Task task2 = new Task("Test task2", "Test task2 description", Duration.ofMinutes(0), LocalDateTime.now());
    public Task task3 = new Task("Test task3", "Test task3 description", Duration.ofMinutes(0), LocalDateTime.now());
    public Epic epic1 = new Epic("Test epic1", "Test epic1 description", Duration.ofMinutes(0), LocalDateTime.now());
    public Epic epic2 = new Epic("Test epic2", "Test epic2 description", Duration.ofMinutes(0), LocalDateTime.now());
    public Epic epic3 = new Epic("Test epic3", "Test epic3 description", Duration.ofMinutes(0), LocalDateTime.now());
    public SubTask subTask1d1 = new SubTask("Test subTask1d1", "Test subTask1d1 description", Duration.ofMinutes(0), LocalDateTime.now());
    public SubTask subTask1d2 = new SubTask("Test subTask1d2", "Test subTask1d2 description", Duration.ofMinutes(0), LocalDateTime.now());
    public SubTask subTask1d3 = new SubTask("Test subTask1d3", "Test subTask1d3 description", Duration.ofMinutes(0), LocalDateTime.now());
    public SubTask subTask2d1 = new SubTask("Test subTask2d1", "Test subTask2d1 description", Duration.ofMinutes(0), LocalDateTime.now());
    public SubTask subTask2d2 = new SubTask("Test subTask2d2", "Test subTask2d2 description", Duration.ofMinutes(0), LocalDateTime.now());
    public SubTask subTask2d3 = new SubTask("Test subTask2d3", "Test subTask2d3 description", Duration.ofMinutes(0), LocalDateTime.now());
    public SubTask subTask3d1 = new SubTask("Test subTask3d1", "Test subTask3d1 description", Duration.ofMinutes(0), LocalDateTime.now());
    public SubTask subTask3d2 = new SubTask("Test subTask3d2", "Test subTask3d2 description", Duration.ofMinutes(0), LocalDateTime.now());
    public SubTask subTask3d3 = new SubTask("Test subTask3d3", "Test subTask3d3 description", Duration.ofMinutes(0), LocalDateTime.now());
    public List<Task> tasks = List.of(task1, task2, task3);
    public List<Epic> epics = List.of(epic1, epic2, epic3);
    public List<SubTask> subTasks = List.of(
            subTask3d3,
            subTask3d2,
            subTask3d1,
            subTask2d3,
            subTask2d2,
            subTask2d1,
            subTask1d3,
            subTask1d2,
            subTask1d1
    );
    public List<SubTask> subTasks1 = List.of(subTask1d1, subTask1d2, subTask1d3);
    public List<SubTask> subTasks2 = List.of(subTask2d1, subTask2d2, subTask2d3);
    public List<SubTask> subTasks3 = List.of(subTask3d1, subTask3d2, subTask3d3);
    public List<List<SubTask>> allSubtasks = List.of(subTasks1, subTasks2, subTasks3);

}
