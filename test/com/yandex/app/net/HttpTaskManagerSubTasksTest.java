package com.yandex.app.net;

import com.google.gson.Gson;
import com.yandex.app.model.SubTask;
import com.yandex.app.model.TasksForTests;
import com.yandex.app.service.InMemoryTaskManager;
import com.yandex.app.service.NotFoundException;
import com.yandex.app.service.Status;
import com.yandex.app.service.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerSubTasksTest {
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
    void subTasksPostCreateSubTaskTest() throws IOException, InterruptedException  {
        try {
            System.out.println("=".repeat(8) + " Test 6 " + "=".repeat(8));

            // Prepared data
            SubTask subTask1d1 = tft.subTask1d1;
            String subTaskJson = gson.toJson(subTask1d1);

            // POST request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/subtasks"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(subTaskJson))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Tests
            assertEquals(201, response.statusCode());

            List<SubTask> subTasksFromManager = taskManager.getSubTasksList();

            assertNotNull(subTasksFromManager, "Задачи не возвращаются");
            assertEquals(1, subTasksFromManager.size(), "Некорректное количество задач");
            assertEquals("Test subTask1d1",
                    subTasksFromManager.getFirst().getName(), "Некорректное имя задачи");

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void subTasksPostUpdateSubTaskTest() throws IOException, InterruptedException  {
        try {
            System.out.println("=".repeat(8) + " Test 7 " + "=".repeat(8));

            // Prepared data
            SubTask subTask1d1 = tft.subTask1d1;
            String subTaskJson = gson.toJson(subTask1d1);

            // POST request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/subtasks"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(subTaskJson))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Prepared data
            subTask1d1.setDescription("Another description");
            subTask1d1.setStatus(Status.IN_PROGRESS);

            int subTaskId = taskManager.getSubTasksList().getFirst().getId();

            subTask1d1.setId(subTaskId);

            // POST request
            String taskJson2 = gson.toJson(subTask1d1);
            HttpRequest request2 = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/subtasks" + '/' + subTaskId))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson2))
                    .build();
            HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

            // Tests
            assertEquals(201, response.statusCode());
            SubTask subTask = taskManager.getSubTasksList().getFirst();

            assertEquals(subTask1d1.getName(), subTask.getName());
            assertEquals(subTask1d1.getDescription(), subTask.getDescription());

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void subTasksGetGetSubTaskTest() throws IOException, InterruptedException  {
        try {
            System.out.println("=".repeat(8) + " Test 8 " + "=".repeat(8));

            // Prepared data
            SubTask subTask = tft.subTask1d1;
            String subTaskJson = gson.toJson(subTask);

            // POST request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/subtasks"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(subTaskJson))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Prepared data
            String subTaskId = String.valueOf(taskManager.getSubTasksList().getFirst().getId());

            // GET request
            HttpRequest request2 = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/subtasks" + '/' + subTaskId))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();
            HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

            // Tests
            SubTask responseSubTask = gson.fromJson(response2.body(), SubTask.class);

            assertEquals(subTask.getName(), responseSubTask.getName());
            assertEquals(subTask.getDescription(), responseSubTask.getDescription());

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void subTasksGetGetSubTasksTest() throws IOException, InterruptedException  {
        try {
            System.out.println("=".repeat(8) + " Test 9 " + "=".repeat(8));

            int subTasksCount = 0;
            for (SubTask subTask : tft.subTasks) {
                subTasksCount++;
                String subTaskJson = gson.toJson(subTask);

                // POST request
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/subtasks"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(subTaskJson))
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                // Tests
                List<SubTask> subTasksFromManager = taskManager.getSubTasksList();

                assertNotNull(subTasksFromManager, "Задачи не возвращаются");
                assertEquals(subTasksCount, subTasksFromManager.size(), "Некорректное количество задач");
                System.out.println("subTasksCount = " + subTasksCount + "; "
                        + "subTasksFromManager.size() = " + subTasksFromManager.size() + "\n");

                // GET request
                HttpRequest request2 = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/subtasks"))
                        .header("Content-Type", "application/json")
                        .GET()
                        .build();
                HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

                // Tests
                List<SubTask> responseSubTasksList = gson.fromJson(response2.body(), new TaskListTypeToken().getType());

                assertNotNull(responseSubTasksList, "Задачи не возвращаются");
                assertEquals(subTasksCount, responseSubTasksList.size(), "Некорректное количество задач");

            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void subTasksDeleteDeleteSubTasksTest() throws IOException, InterruptedException  {
        boolean thrownSubTasksCount = false;
        int subTasksCount;

        try {
            System.out.println("=".repeat(8) + " Test 10 " + "=".repeat(8));

            // Prepared data
            SubTask subTask = tft.subTask1d1;
            String subTaskJson = gson.toJson(subTask);

            // POST request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/subtasks"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(subTaskJson))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Prepared data
            String subTaskId = String.valueOf(taskManager.getSubTasksList().getFirst().getId());

            // Tests
            assertNotNull(taskManager.getSubTasksList(), "Задачи не возвращаются");
            assertEquals("Test subTask1d1",
                    taskManager.getSubTasksList().getFirst().getName(), "Некорректное имя задачи");

            // DELETE request
            HttpRequest request2 = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/subtasks" + '/' + subTaskId))
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
            subTasksCount = taskManager.getSubTasksList().size();
        } catch (NotFoundException e) {
            thrownSubTasksCount = true;
        }

        assertTrue(thrownSubTasksCount);
    }
}
