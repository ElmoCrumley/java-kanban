package com.yandex.app.service;

import com.yandex.app.model.Task;
import com.yandex.app.model.Epic;
import com.yandex.app.model.SubTask;
import com.yandex.app.model.TasksForTests;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import java.util.List;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    T taskManager;
    TasksForTests tft;

    @Test
    public void testMultipleAssertionsWithStreamForCreateAnyTask() {
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
    public void testAssertionsWithStreamForRemoveAllTask() {
        List<Task> tasks = taskManager.getTasksList();
        List<Epic> epics = taskManager.getEpicsList();
        List<SubTask> subTasks = taskManager.getSubTasksList();

        assertEquals(3, tasks.size());
        assertEquals(3, epics.size());
        assertEquals(9, subTasks.size());
        taskManager.removeTasks();
        taskManager.removeSubTasks();
        taskManager.removeEpics();

        boolean thrownTasks = false;
        boolean thrownEpics = false;
        boolean thrownSubTasks = false;

        try {
            tasks = taskManager.getTasksList();
        } catch (NotFoundException e) {
            thrownTasks = true;
        }

        try {
            epics = taskManager.getEpicsList();
        } catch (NotFoundException e) {
            thrownEpics = true;
        }

        try {
            subTasks = taskManager.getSubTasksList();
        } catch (NotFoundException e) {
            thrownSubTasks = true;
        }

        assertTrue(thrownTasks);
        assertTrue(thrownEpics);
        assertTrue(thrownSubTasks);
    }

    @Test
    public void testMultipleAssertionsWithStreamForGetAnyTask() {
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
                () -> assertEquals(tft.task1, fixedTask1),
                () -> assertEquals(tft.epic1, fixedEpic1),
                () -> assertEquals(tft.subTask1d1, fixedSubTask1)
        );

        assertAll("Checking update tests for Tasks, Epics, Subtasks", executables);
    }

    @Test
    public void testMultipleAssertionsWithStreamForRemove() {
        int task1Id = tft.task1.getId();
        int epic2Id = tft.epic2.getId();
        int subTask1d1Id = tft.subTask1d1.getId();

        taskManager.removeTask(task1Id);
        taskManager.removeEpic(epic2Id);
        taskManager.removeSubTask(subTask1d1Id);

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
    public void testAssertionsWithStreamForClearAllTasks() {
        int tasksCount = taskManager.getTasksList().size();
        int epicsCount = taskManager.getEpicsList().size();
        int subTasksCount = taskManager.getSubTasksList().size();

        taskManager.clearAllTasks();

        boolean thrownTasksCount = false;
        boolean thrownEpicsCount = false;
        boolean thrownSubTasksCount = false;

        try {
            tasksCount = taskManager.getTasksList().size();
        } catch (NotFoundException e) {
            thrownTasksCount = true;
        }

        try {
            epicsCount = taskManager.getEpicsList().size();
        } catch (NotFoundException e) {
            thrownEpicsCount = true;
        }

        try {
            subTasksCount = taskManager.getSubTasksList().size();
        } catch (NotFoundException e) {
            thrownSubTasksCount = true;
        }

        assertTrue(thrownTasksCount);
        assertTrue(thrownEpicsCount);
        assertTrue(thrownSubTasksCount);
    }

    @Test
    public void testForGetHistoryManager() {
        HistoryManager historyManager = taskManager.getHistoryManager();

        assertNotNull(historyManager);
        assertEquals(historyManager, taskManager.getHistoryManager());
    }
}
