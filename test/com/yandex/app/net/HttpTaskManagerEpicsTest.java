package com.yandex.app.net;

import com.google.gson.Gson;
import com.yandex.app.model.Epic;
import com.yandex.app.model.SubTask;
import com.yandex.app.model.TasksForTests;
import com.yandex.app.service.InMemoryTaskManager;
import com.yandex.app.service.NotFoundException;
import com.yandex.app.service.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerEpicsTest {
    TasksForTests tft;
    TaskManager taskManager = new InMemoryTaskManager();
    Gson gson = HttpTaskServer.getGson();
    HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
    HttpClient client = HttpClient.newHttpClient();

    @BeforeEach
    public void setUp() throws IOException {
        taskManager.removeTasks();
        taskManager.removeEpics();
        tft = new TasksForTests();
        httpTaskServer.start();
    }

    @AfterEach
    public void shutDown() {
        httpTaskServer.stop();
    }

    @Test
    void epicsPostCreateEpicTest() throws IOException, InterruptedException  {
        try {
            System.out.println("=".repeat(8) + " Test 11 " + "=".repeat(8));

            // Prepared data
            Epic epic1 = new Epic("Name", "Description", Duration.ofMinutes(5), LocalDateTime.now());
            String epicJson = gson.toJson(epic1);

            // POST request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/epics"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Tests
            assertEquals(201, response.statusCode());

            List<Epic> epicsFromManager = taskManager.getEpicsList();

            assertNotNull(epicsFromManager, "Задачи не возвращаются");
            assertEquals(1, epicsFromManager.size(), "Некорректное количество задач");
            assertEquals("Name", epicsFromManager.getFirst().getName(), "Некорректное имя задачи");

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void epicsGetGEtEpicByIdTest() throws IOException, InterruptedException  {
        try {
            System.out.println("=".repeat(8) + " Test 12 " + "=".repeat(8));

            // Prepared data
            Epic epic = tft.epic1;
            String epicJson = gson.toJson(epic);

            // POST request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/epics"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Prepared data
            int epicsId = taskManager.getEpicsList().getFirst().getId();
            SubTask subTask1d1 = tft.subTask1d1;

            subTask1d1.setEpicsId(epicsId);
            String subTaskJson = gson.toJson(subTask1d1);

            // POST request
            HttpRequest request2 = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/subtasks"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(subTaskJson))
                    .build();
            HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());


            // GET request
            HttpRequest request3 = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/epics" + '/' + epicsId))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();
            HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());

            // Tests
            Epic responseEpic = gson.fromJson(response3.body(), Epic.class);

            assertEquals(epic.getName(), responseEpic.getName());
            assertEquals(epic.getDescription(), responseEpic.getDescription());
            assertEquals(1, taskManager.getEpicsList().getFirst().getSubTasksList().size());

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void epicsGetGetEpicsTest() throws IOException, InterruptedException  {
        try {
            System.out.println("=".repeat(8) + " Test 13 " + "=".repeat(8));

            int epicsCount = 0;
            for (Epic epic : tft.epics) {
                epicsCount++;
                String epicJson = gson.toJson(epic);

                // POST request
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/epics"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                // Tests
                List<Epic> epicsFromManager = taskManager.getEpicsList();

                assertNotNull(epicsFromManager, "Задачи не возвращаются");
                assertEquals(epicsCount, epicsFromManager.size(), "Некорректное количество задач");
                System.out.println("epicsCount = " + epicsCount + "; "
                        + "epicsFromManager.size() = " + epicsFromManager.size() + "\n");

                // GET request
                HttpRequest request2 = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/epics"))
                        .header("Content-Type", "application/json")
                        .GET()
                        .build();
                HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

                // Tests
                List<Epic> responseEpicsList = gson.fromJson(response2.body(), new TaskListTypeToken().getType());

                assertNotNull(responseEpicsList, "Задачи не возвращаются");
                assertEquals(epicsCount, responseEpicsList.size(), "Некорректное количество задач");

            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void epicsGetGetEpicSubTasksTest() throws IOException, InterruptedException  {
        try {
            System.out.println("=".repeat(8) + " Test 14 " + "=".repeat(8));

            // Prepared data
            Epic epic = tft.epic1;
            String epicJson = gson.toJson(epic);

            // POST request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/epics"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Prepared data
            int epicsId = taskManager.getEpicsList().getFirst().getId();
            List<SubTask> subTasks = tft.subTasks1;

            for (SubTask subTask : subTasks) {
                subTask.setEpicsId(epicsId);
                String subTaskJson = gson.toJson(subTask);

                // POST request
                HttpRequest request2 = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/subtasks"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(subTaskJson))
                        .build();
                HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
            }

            // GET request
            HttpRequest request3 = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/epics" + '/' + epicsId + "/subtasks"))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();
            HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());

            // Tests
            List<SubTask> responseEpicSubTasksList = gson.fromJson(response3.body(), new TaskListTypeToken().getType());

            assertEquals(responseEpicSubTasksList.size(),
                    taskManager.getEpicsList().getFirst().getSubTasksList().size());

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void epicsDeleteDeleteEpicTest() throws IOException, InterruptedException {
        boolean thrownEpicsCount = false;
        int epicsCount;

        try {
            System.out.println("=".repeat(8) + " Test 15 " + "=".repeat(8));

            // Prepared data
            Epic epic1 = new Epic("Name", "Description", Duration.ofMinutes(5), LocalDateTime.now());
            String epicJson = gson.toJson(epic1);

            // POST request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/epics"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Prepared data
            String epicId = String.valueOf(taskManager.getEpicsList().getFirst().getId());

            // Tests
            assertNotNull(taskManager.getEpicsList(), "Задачи не возвращаются");
            assertEquals("Name",
                    taskManager.getEpicsList().getFirst().getName(), "Некорректное имя задачи");

            // DELETE request
            HttpRequest request2 = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/epics" + '/' + epicId))
                    .header("Content-Type", "application/json")
                    .DELETE()
                    .build();
            HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Tests
        try {
            epicsCount = taskManager.getEpicsList().size();
        } catch (NotFoundException e) {
            thrownEpicsCount = true;
        }

        assertTrue(thrownEpicsCount);
    }
}
