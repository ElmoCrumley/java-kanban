package com.yandex.app.service;
import com.yandex.app.model.Epic;
import com.yandex.app.model.Task;
import com.yandex.app.model.TasksForTests;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

class InMemoryHistoryManagerTest extends HistoryManagerTest {
    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
        historyManager = taskManager.getHistoryManager();
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
    public void addTaskTest() {
        testForAddTask();
    }

    @Test
    public void removeTest() {
        testForRemove();
    }

    @Test
    public void getHistoryTest() {
        testForGetHistory();
    }
}