package com.yandex.app.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @BeforeEach
    public void setUp() {
        taskManager = new InMemoryTaskManager();
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
    public void removeMethodsTest() {
        testMultipleAssertionsWithStreamForRemove();
    }

    @Test
    public void getTaskMethodsTest() {
        testMultipleAssertionsWithStreamForGetAnyTask();
    }

    @Test
    public void updateTaskMethodsTest() {
        testMultipleAssertionsWithStreamForUpdateAnyTask();
    }
//    @Test
//    public void
}
