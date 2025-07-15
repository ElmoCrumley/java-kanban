package com.yandex.app.net;

import com.google.gson.Gson;
import com.yandex.app.model.Task;
import com.yandex.app.model.TasksForTests;
import com.yandex.app.service.InMemoryTaskManager;
import com.yandex.app.service.TaskManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class HttpTaskManagerTasksTest {
    TasksForTests tft;
    TaskManager taskManager = new InMemoryTaskManager();
    HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
    Gson gson;

    @BeforeEach
    public void setUp() throws IOException {
        taskManager.removeTasks();
        taskManager.removeEpics();
        tft = new TasksForTests();
        gson = HttpTaskServer.getGson();
        httpTaskServer.start();
    }

    @AfterEach
    public void shutDown() {
        httpTaskServer.stop();
    }

    @Test
    public void tasksPostCreateTaskTest()  throws IOException, InterruptedException  {
        Task task1 = tft.task1;
        String taskJson = gson.toJson(task1);
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        // HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = taskManager.getTasksList();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test task1", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

//    @Test
//    void tasksPostUpdateTaskTest() throws IOException, InterruptedException  {
//
//    }
//
//    @Test
//    void tasksGetGetTaskTest() throws IOException, InterruptedException  {
//
//    }
//
//    @Test
//    void tasksGetGetTasksTest() throws IOException, InterruptedException  {
//
//    }
//
//    @Test
//    void tasksDeleteDeleteTaskTest() throws IOException, InterruptedException  {
//
//    }
//
//    @Test
//    void subTasksPostCreateSubTaskTest() throws IOException, InterruptedException  {
//
//    }
//
//    @Test
//    void subTasksPostUpdateSubTaskTest() throws IOException, InterruptedException  {
//
//    }
//
//    @Test
//    void subTasksGetGetSubTaskTest() throws IOException, InterruptedException  {
//
//    }
//
//    @Test
//    void subTasksGetGetSubTasksTest() throws IOException, InterruptedException  {
//
//    }
//
//    @Test
//    void subTasksDeleteDeleteSubTasksTest() throws IOException, InterruptedException  {
//
//    }
//
//    @Test
//    void epicsPostCreateEpicTest() throws IOException, InterruptedException  {
//
//    }
//
//    @Test
//    void epicsGetGEtEpicByIdTest() throws IOException, InterruptedException  {
//
//    }
//
//    @Test
//    void epicsGetGetEpicsTest() throws IOException, InterruptedException  {
//
//    }
//
//    @Test
//    void epicsGetGetEpicSubTasksTest() throws IOException, InterruptedException  {
//
//    }
//
//    @Test
//    void epicsDeleteDeleteEpicTest() throws IOException, InterruptedException {
//
//    }
//
//    @Test
//    void historyGetGetHistoryTest() throws IOException, InterruptedException  {
//
//    }
//
//    @Test
//    void prioritizedGetGetPrioritizedTasksTest() throws IOException, InterruptedException {
//
//    }
}
