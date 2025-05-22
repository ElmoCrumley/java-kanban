package com.yandex.app.service;

import com.yandex.app.model.Task;
import com.yandex.app.model.Epic;
import com.yandex.app.model.SubTask;
import com.yandex.app.model.TasksForTests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    T taskManager;
    TasksForTests tft;

    @BeforeEach
    public void beforeEach() throws IOException {
        tft = new TasksForTests();
    }

    @Test
    public void testMultipleAssertionsWithStreamForCreateAnyTask() {
        for (Task task : tft.tasks) {
            taskManager.createTask(task);
        }
        for (Epic epic : tft.epics) {
            taskManager.createEpic(epic);
        }
        for (int i = 0; i < tft.allSubtasks.size(); i++) {
            for (int j = 0; j < tft.allSubtasks.get(i).size(); j++) {
                taskManager.createSubTask(tft.allSubtasks.get(i).get(j), tft.epics.get(i).getId());
            }
        }

        Task copyOfTask1 = tft.task1;
        Epic copyOfEpic1 = tft.epic1;
        SubTask copyOfSubtask1 = tft.subTask1d1;

        Stream<Executable> executables = Stream.of(
                () -> assertNotNull(copyOfTask1),
                () -> assertNotNull(copyOfEpic1),
                () -> assertNotNull(copyOfSubtask1),
                () -> assertEquals(tft.task1, copyOfTask1),
                () -> assertEquals(tft.epic1, copyOfEpic1),
                () -> assertEquals(tft.subTask1d1, copyOfSubtask1)
        );

        assertAll("Checking create tests for Tasks, Epics, Subtasks", executables);
    }

    @Test
    public void testMultipleAssertionsWithStreamForGetList() {
        for (Task task : tft.tasks) {
            taskManager.createTask(task);
        }
        for (Epic epic : tft.epics) {
            taskManager.createEpic(epic);
        }
        for (int i = 0; i < tft.allSubtasks.size(); i++) {
            for (int j = 0; j < tft.allSubtasks.get(i).size(); j++) {
                taskManager.createSubTask(tft.allSubtasks.get(i).get(j), tft.epics.get(i).getId());
            }
        }

        int tasksSize = taskManager.getTasksList().size();
        int epicsSize = taskManager.getEpicsList().size();
        int subTasksSize = taskManager.getSubTasksList().size();

        Stream<Executable> executables = Stream.of(
                () -> assertEquals(3, tasksSize),
                () -> assertEquals(3, epicsSize),
                () -> assertEquals(9, subTasksSize)
        );

        assertAll("Checking getList tests for Tasks, Epics, Subtasks", executables);
    }

    @Test
    public void testMultipleAssertionsWithStreamForRemove() {
        for (Task task : tft.tasks) {
            taskManager.createTask(task);
        }
        for (Epic epic : tft.epics) {
            taskManager.createEpic(epic);
        }
        for (int i = 0; i < tft.allSubtasks.size(); i++) {
            for (int j = 0; j < tft.allSubtasks.get(i).size(); j++) {
                taskManager.createSubTask(tft.allSubtasks.get(i).get(j), tft.epics.get(i).getId());
            }
        }

        taskManager.removeTask(tft.task1.getId());
        taskManager.removeEpic(tft.epic1.getId());
        taskManager.removeSubTask(tft.subTask2d1.getId());

        int tasksSize = taskManager.getTasksList().size();
        int epicsSize = taskManager.getEpicsList().size();
        int subTasksSize = taskManager.getSubTasksList().size();

        Stream<Executable> executables = Stream.of(
                () -> assertEquals(2, tasksSize),
                () -> assertEquals(2, epicsSize),
                () -> assertEquals(5, subTasksSize) // Учитывать удалённый эпик, в котором 3 подзадачи
        );

        assertAll("Checking remove tests for Tasks, Epics, Subtasks", executables);
    }

    @Test
    public void testMultipleAssertionsWithStreamForGetAnyTask() {
        for (Task task : tft.tasks) {
            taskManager.createTask(task);
        }
        for (Epic epic : tft.epics) {
            taskManager.createEpic(epic);
        }
        for (int i = 0; i < tft.allSubtasks.size(); i++) {
            for (int j = 0; j < tft.allSubtasks.get(i).size(); j++) {
                taskManager.createSubTask(tft.allSubtasks.get(i).get(j), tft.epics.get(i).getId());
            }
        }

        Task copyOfTask1 = taskManager.getTask(tft.task1.getId());
        Epic copyOfEpic1 = taskManager.getEpic(tft.epic1.getId());
        SubTask copyOfSubtask1 = taskManager.getSubTask(tft.subTask1d1.getId());

        Stream<Executable> executables = Stream.of(
                () -> assertEquals(tft.task1, copyOfTask1),
                () -> assertEquals(tft.epic1, copyOfEpic1),
                () -> assertEquals(tft.subTask1d1, copyOfSubtask1)
        );

        assertAll("Checking get tests for Tasks, Epics, Subtasks", executables);
    }

    @Test
    public void testMultipleAssertionsWithStreamForUpdateAnyTask() {
        for (Task task : tft.tasks) {
            taskManager.createTask(task);
        }
        for (Epic epic : tft.epics) {
            taskManager.createEpic(epic);
        }
        for (int i = 0; i < tft.allSubtasks.size(); i++) {
            for (int j = 0; j < tft.allSubtasks.get(i).size(); j++) {
                taskManager.createSubTask(tft.allSubtasks.get(i).get(j), tft.epics.get(i).getId());
            }
        }

        Task fixedTask1 = taskManager.getTask(tft.task1.getId());
        Epic fixedEpic1 = taskManager.getEpic(tft.epic1.getId());
        SubTask fixedSubTask1 = taskManager.getSubTask(tft.subTask1d1.getId());

        taskManager.getTask(tft.task1.getId()).setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(tft.task1);
        taskManager.getSubTask(tft.subTask1d1.getId()).setStatus(Status.IN_PROGRESS);
        taskManager.updateSubTask(tft.subTask1d1);

        Stream<Executable> executables = Stream.of(
                () -> assertNotNull(fixedTask1),
                () -> assertNotNull(fixedEpic1),
                () -> assertNotNull(fixedSubTask1),
                () -> assertNotEquals(tft.task1, fixedTask1),
                () -> assertNotEquals(tft.epic1, fixedEpic1),
                () -> assertNotEquals(tft.subTask1d1, fixedSubTask1)
        );

        assertAll("Checking get tests for Tasks, Epics, Subtasks", executables);
    }
//
//    // Обновление.
//    @Test
//    public void updateTaskTest() {
//
//    }
//
//    @Test
//    public void updateSubTaskTest() {
//
//    }
//
//    // Удаление по идентификатору.
//    @Test
//    public void removeTaskTest() {
//
//    }
//
//    @Test
//    public void removeEpicTest() {
//
//    }
//
//    @Test
//    public void removeSubTaskTest() {
//
//    }
//
//    @Test
//    public void clearAllTasksTest() {
//
//    }
//
//    @Test
//    public void getHistoryManagerTest() {
//
//    }
}
