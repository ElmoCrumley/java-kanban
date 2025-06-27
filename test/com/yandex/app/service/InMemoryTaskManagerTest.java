package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.SubTask;
import com.yandex.app.model.Task;
import com.yandex.app.model.TasksForTests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @BeforeEach
    public void setUp() {
        taskManager = new InMemoryTaskManager();
        tft = new TasksForTests();

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
    }

    @Test
    public void createTaskMethodsTest() {
        testMultipleAssertionsWithStreamForCreateAnyTask();
    }

    @Test
    public void getTasksListMethodsTest() {
        testMultipleAssertionsWithStreamForGetList();
    }

    @Test
    public void removeAllTaskMethodsTest() {
        testMultipleAssertionsWithStreamForRemoveAllTask();
    }

    @Test
    public void getTaskMethodsTest() {
        testMultipleAssertionsWithStreamForGetAnyTask();
    }

    @Test
    public void updateTaskMethodsTest() {
        testMultipleAssertionsWithStreamForUpdateAnyTask();
    }

    @Test
    public void removeMethodsTest() {
        testMultipleAssertionsWithStreamForRemove();
    }

    @Test
    public void clearAllTasks() {
        testMultipleAssertionsWithStreamForClearAllTasks();
    }

    @Test
    public void getHistoryManagerTest() {
        testForGetHistoryManager();
    }

    @Test
    public void getTasksEpicsSubTasksTests() {
        Map<Integer, Task> tasks = taskManager.getTasks();
        Map<Integer, Epic> epics = taskManager.getEpics();
        Map<Integer, SubTask> subTasks = taskManager.getSubTasks();

        Stream<Executable> executables = Stream.of(
                () -> assertNotNull(tasks),
                () -> assertNotNull(epics),
                () -> assertNotNull(subTasks),
                () -> assertEquals(tasks, taskManager.getTasks()),
                () -> assertEquals(epics, taskManager.getEpics()),
                () -> assertEquals(subTasks, taskManager.getSubTasks())
        );

        assertAll("Checking getMap tests for Tasks, Epics, Subtasks", executables);
    }

    @Test
    public void getEpicsSubTasksListTest() {
        ArrayList<SubTask> subTasksList1 = InMemoryTaskManager.getEpicsSubTasksList(tft.epic1);

        Stream<Executable> executables = Stream.of(
                () -> assertNotNull(subTasksList1),
                () -> assertEquals(subTasksList1, InMemoryTaskManager.getEpicsSubTasksList(tft.epic1)),
                () -> assertEquals(3, subTasksList1.size())
        );

        assertAll("Checking getEpicsSubTasksList test for Epics", executables);
    }

    @Test
    public void dataComparatorTest() {
        Task task1 = tft.task1;
        Task task2 = tft.task2;
        Task task3 = tft.task3;

        task1.setStartTime(LocalDateTime.of(2025, 1, 1, 11, 0)); // 00 -> 20
        task1.setDuration(20);
        task2.setStartTime(LocalDateTime.of(2025, 1, 1, 11, 15)); // 15 -> 35
        task2.setDuration(20);
        task3.setStartTime(LocalDateTime.of(2025, 1, 1, 11, 21)); // 21 -> 41
        task3.setDuration(20);

        Stream<Executable> executables = Stream.of(
                () -> assertFalse(task1.isIntersect(task3)),
                () -> assertTrue(task2.isIntersect(task1)),
                () -> assertTrue(task2.isIntersect(task3))
        );

        assertAll("Checking dataComparator test", executables);
    }

    @Test
    public void getPrioritizedTasksTest() {
        Task task1 = new Task("Test task1", "Test task1 description");
        Task task2 = new Task("Test task2", "Test task2 description");
        Task task3 = new Task("Test task3", "Test task3 description");

        task1.setStartTime(LocalDateTime.of(2025, 1, 1, 11, 50)); // 00 -> 20
        task1.setDuration(10);
        task2.setStartTime(LocalDateTime.of(2025, 1, 1, 11, 30)); // 15 -> 35
        task2.setDuration(10);
        task3.setStartTime(LocalDateTime.of(2025, 1, 1, 11, 10)); // 21 -> 41
        task3.setDuration(10);
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        ArrayList<Task> prioritizedTasks = taskManager.getPrioritizedTasks();

        Task taskFirst = prioritizedTasks.get(0);
        Task taskASecond = prioritizedTasks.get(1);
        Task taskThird = prioritizedTasks.get(2);

        Stream<Executable> executables = Stream.of(
                () -> assertNotNull(prioritizedTasks),
                () -> assertEquals(3, prioritizedTasks.size()),
                () -> assertEquals(taskFirst, task3),
                () -> assertEquals(taskASecond, task2),
                () -> assertEquals(taskThird, task1)
        );

        assertAll("Checking prioritization Tasks test", executables);
    }
}
