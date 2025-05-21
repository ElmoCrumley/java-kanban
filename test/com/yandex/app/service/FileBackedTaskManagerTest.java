package com.yandex.app.service;

import com.yandex.app.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.*;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    Task task;
    Task task2;
    Task task3;
    File log;
    TaskManager fileBackedTaskManager;
    TaskManager fileBackedTaskManager2;


    @BeforeEach
    void beforeEach() throws IOException {
        task = new Task("Test addNewTask", "Test addNewTask description");
        task2 = new Task("Test addNewTask", "Test addNewTask description");
        task3 = new Task("Test addNewTask", "Test addNewTask description");

        try {
            log = File.createTempFile("myTempFile", ".txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        fileBackedTaskManager = Managers.getDefault(log.getAbsoluteFile());
    }

    @Test
    void createTask() throws IOException {
        assertNotNull(log);

        fileBackedTaskManager.createTask(task);
        fileBackedTaskManager.createTask(task2);
        fileBackedTaskManager.createTask(task3);
        System.out.println(fileBackedTaskManager.getTasksList());

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