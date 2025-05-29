package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.Task;
import com.yandex.app.model.TasksForTests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.*;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends InMemoryTaskManager {
//    @BeforeEach
//    public void setUp() {
//        taskManager = new FileBackedTaskManager();
//        tft = new TasksForTests();
//
//        for (Task task : tft.tasks) {
//            taskManager.createTask(task);
//        }
//        for (Epic epic : tft.epics) {
//            taskManager.createEpic(epic);
//        }
//        for (int i = 0; i < tft.allSubtasks.size(); i++) {
//            for (int j = 0; j < tft.allSubtasks.get(i).size(); j++) {
//                taskManager.createSubTask(tft.allSubtasks.get(i).get(j), tft.epics.get(i).getId());
//            }
//        }
//    }

    File log;

    @BeforeEach
    void beforeEach() throws IOException {
        try {
            log = File.createTempFile("myTempFile", ".txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        taskManager = new FileBackedTaskManager(log.getAbsoluteFile());
    }

    @Test
    void createTask() throws IOException {
        assertNotNull(log);

        taskManager.createTask(task);
        taskManager.createTask(task2);
        taskManager.createTask(task3);
        System.out.println(taskManager.getTasksList());

        Reader fileReader = new FileReader(log.getAbsolutePath());
        BufferedReader br = new BufferedReader(fileReader);

        while (br.ready()) {
            String split = br.readLine();
            System.out.println(split);
        }
        fileReader.close();
        br.close();
        fileBackedTaskManager2 = FileBackedTaskManager.loadFromFile(log);

        ArrayList<Task> tasks = fileBackedTaskManager2.getTasksList();

        System.out.println(tasks);

        assertEquals(fileBackedTaskManager.getTasksList().getFirst(), fileBackedTaskManager2.getTasksList().getFirst());
        assertEquals(fileBackedTaskManager.getTasksList().getLast(), fileBackedTaskManager2.getTasksList().getLast());
    }
}