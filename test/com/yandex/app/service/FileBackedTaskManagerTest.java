package com.yandex.app.service;

import com.yandex.app.model.Epic;
import com.yandex.app.model.SubTask;
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
    TaskManager fileBackedtaskManager;
    TaskManager fileBackedtaskManager2;


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

        fileBackedtaskManager = Managers.getDefaultSave(log.getAbsoluteFile());
    }

    @Test
    void createTask() throws IOException {
        assertNotNull(log);

        fileBackedtaskManager.createTask(task);
        fileBackedtaskManager.createTask(task2);
        fileBackedtaskManager.createTask(task3);
        System.out.println(fileBackedtaskManager.getTasksList());

        Reader fileReader = new FileReader(log.getName());
        BufferedReader br = new BufferedReader(fileReader);
        while (br.ready()) {
            String split = br.readLine();
            System.out.println(split);
        }
        fileReader.close();
        br.close();

//        try {
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        fileBackedtaskManager2 = FileBackedTaskManager.loadFromFile(log);
        ArrayList<Task> tasks = fileBackedtaskManager2.getTasksList();
        System.out.println(tasks);


    }
}