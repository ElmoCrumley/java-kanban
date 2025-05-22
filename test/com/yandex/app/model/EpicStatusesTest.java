package com.yandex.app.model;

import com.yandex.app.service.Managers;
import com.yandex.app.service.Status;
import com.yandex.app.service.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EpicStatusesTest  {
    Epic epic;
    SubTask subTask1;
    SubTask subTask2;
    SubTask subTask3;
    List<SubTask> subTasks;
    File log;
    TaskManager fileBackedTaskManager;
    int epicId;

    @BeforeEach
    void beforeEach() throws IOException {
        epic = new Epic("Test epic", "Test epic description");
        subTask1 = new SubTask("Test addNewSubTask1", "Test addNewSubTask1 description");
        subTask2 = new SubTask("Test addNewSubTask2", "Test addNewSubTask2 description");
        subTask3 = new SubTask("Test addNewSubTask3", "Test addNewSubTask3 description");

        subTasks = List.of(subTask1, subTask2, subTask3);

        try {
            log = File.createTempFile("myTempFile", ".txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        fileBackedTaskManager = Managers.getDefault(log.getAbsoluteFile());
        fileBackedTaskManager.createEpic(epic);
        epicId = epic.getId();
    }

    @Test
    void setAllNewStatuses() {
        for (SubTask subTask : subTasks) {
            fileBackedTaskManager.createSubTask(subTask, epicId);
            subTask.setStatus(Status.NEW);
            fileBackedTaskManager.updateSubTask(subTask);
        }

        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    void setAllDoneStatuses() {
        for (SubTask subTask : subTasks) {
            fileBackedTaskManager.createSubTask(subTask, epicId);
            subTask.setStatus(Status.DONE);
            fileBackedTaskManager.updateSubTask(subTask);
        }

        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    void setDifferentNewAndDoneStatuses() {
        for (SubTask subTask : subTasks) {
            fileBackedTaskManager.createSubTask(subTask, epicId);
        }

        subTask1.setStatus(Status.DONE);
        subTask2.setStatus(Status.NEW);
        subTask3.setStatus(Status.DONE);

        for (SubTask subTask : subTasks) {
            fileBackedTaskManager.updateSubTask(subTask);
        }

        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void setAllInProgressStatuses() {
        for (SubTask subTask : subTasks) {
            fileBackedTaskManager.createSubTask(subTask, epicId);
            subTask.setStatus(Status.IN_PROGRESS);
            fileBackedTaskManager.updateSubTask(subTask);
        }

        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }
}
