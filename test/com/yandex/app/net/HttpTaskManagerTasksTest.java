package com.yandex.app.net;

import com.google.gson.Gson;
import com.yandex.app.model.Task;
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
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerTasksTest {
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
    public void tasksPostCreateTaskTest()  throws IOException, InterruptedException  {
        try {
            System.out.println("=".repeat(8) + " Test 1 " + "=".repeat(8));

            // Prepared data
            Task task1 = new Task("Name", "Description", Duration.ofMinutes(5), LocalDateTime.now());
            String taskJson = gson.toJson(task1);

            // POST request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/tasks"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Tests
            assertEquals(201, response.statusCode());

            List<Task> tasksFromManager = taskManager.getTasksList();

            assertNotNull(tasksFromManager, "Задачи не возвращаются");
            assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
            assertEquals("Name", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void tasksPostUpdateTaskTest() throws IOException, InterruptedException  {
        try {
            System.out.println("=".repeat(8) + " Test 2 " + "=".repeat(8));

            // Prepared data
            String taskJson;
            Task task1 = new Task("Name", "Description", Duration.ofMinutes(5), LocalDateTime.now());
            taskJson = gson.toJson(task1);

            // POST request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/tasks"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Prepared data
            task1 = taskManager.getTasksList().getFirst();
            task1.setStatus(Status.IN_PROGRESS);
            task1.setDescription("Another description");
            taskJson = gson.toJson(task1);

            // POST request
            HttpRequest request2 = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/tasks/293508254"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                    .build();
            HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

            // Tests
            assertEquals(201, response.statusCode());

            List<Task> tasksFromManager = taskManager.getTasksList();

            assertNotNull(tasksFromManager, "Задачи не возвращаются");
            assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
            assertEquals("Another description",
                    tasksFromManager.getFirst().getDescription(), "Неверное новое описание задачи");

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void tasksGetGetTaskTest() throws IOException, InterruptedException  {
        try {
            System.out.println("=".repeat(8) + " Test 3 " + "=".repeat(8));

            // Prepared data
            Task task = tft.task1;
            String taskJson = gson.toJson(task);

            // POST request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/tasks"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Prepared data
            String taskId = String.valueOf(taskManager.getTasksList().getFirst().getId());

            // GET request
            HttpRequest request2 = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/tasks" + '/' + taskId))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();
            HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

            // Tests
            Task responseTask = gson.fromJson(response2.body(), Task.class);

            assertEquals(task.getName(), responseTask.getName());
            assertEquals(task.getDescription(), responseTask.getDescription());

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void tasksGetGetTasksTest() throws IOException, InterruptedException  {
        try {
            System.out.println("=".repeat(8) + " Test 4 " + "=".repeat(8));

            int taskCount = 0;
            for (Task task : tft.tasks) {
                taskCount++;
                String taskJson = gson.toJson(task);

                // POST request
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/tasks"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                // Tests
                List<Task> tasksFromManager = taskManager.getTasksList();

                assertNotNull(tasksFromManager, "Задачи не возвращаются");
                assertEquals(taskCount, tasksFromManager.size(), "Некорректное количество задач");
                System.out.println("taskCount = " + taskCount + "; "
                        + "tasksFromManager.size() = " + tasksFromManager.size() + "\n");

                // GET request
                HttpRequest request2 = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/tasks"))
                        .header("Content-Type", "application/json")
                        .GET()
                        .build();
                HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

                // Tests
                List<Task> responseTasksList = gson.fromJson(response2.body(), new TaskListTypeToken().getType());

                assertNotNull(responseTasksList, "Задачи не возвращаются");
                assertEquals(taskCount, responseTasksList.size(), "Некорректное количество задач");
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void tasksDeleteDeleteTaskTest() throws IOException, InterruptedException  {
        boolean thrownTasksCount = false;
        int tasksCount;

        try {
            System.out.println("=".repeat(8) + " Test 5 " + "=".repeat(8));

            // Prepared data
            Task task = tft.task1;
            String taskJson = gson.toJson(task);

            // POST request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/tasks"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Prepared data
            String taskId = String.valueOf(taskManager.getTasksList().getFirst().getId());

            // Tests
            assertNotNull(taskManager.getTasksList(), "Задачи не возвращаются");
            assertEquals("Test task1",
                    taskManager.getTasksList().getFirst().getName(), "Некорректное имя задачи");

            // DELETE request
            HttpRequest request2 = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/tasks" + '/' + taskId))
                    .header("Content-Type", "application/json")
                    .DELETE()
                    .build();
            HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Tests
        try {
            tasksCount = taskManager.getSubTasksList().size();
        } catch (NotFoundException e) {
            thrownTasksCount = true;
        }

        assertTrue(thrownTasksCount);
    }
}
