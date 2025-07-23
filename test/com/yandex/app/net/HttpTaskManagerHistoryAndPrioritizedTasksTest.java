package com.yandex.app.net;

import com.google.gson.Gson;
import com.yandex.app.model.Task;
import com.yandex.app.model.TasksForTests;
import com.yandex.app.service.InMemoryTaskManager;
import com.yandex.app.service.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskManagerHistoryAndPrioritizedTasksTest {
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
    void historyGetGetHistoryTest() throws IOException, InterruptedException  {
        try {
            System.out.println("=".repeat(8) + " Test 16 " + "=".repeat(8));

            for (Task task : tft.tasks) {
                String taskJson = gson.toJson(task);

                // POST request
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/tasks"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            }

            int taskCount = 0;
            for (Task task : taskManager.getTasksList()) {
                taskCount++;
                int taskId = task.getId();

                // GET request
                HttpRequest request2 = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/tasks" + '/' + taskId))
                        .header("Content-Type", "application/json")
                        .GET()
                        .build();
                HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

                // GET request
                HttpRequest request3 = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/history"))
                        .header("Content-Type", "application/json")
                        .GET()
                        .build();
                HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());

                // Tests
                List<Task> responseTasksList = gson.fromJson(response3.body(), new TaskListTypeToken().getType());

                assertEquals(200, response3.statusCode());
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
    void prioritizedGetGetPrioritizedTasksTest() throws IOException, InterruptedException {
        try {
            System.out.println("=".repeat(8) + " Test 17 " + "=".repeat(8));

            // Prepared data
            List<Task> taskList = List.of(
                    new Task("Test task1", "Test task1 description",
                            Duration.ofMinutes(10),
                            LocalDateTime.of(2025, 1, 1, 11, 50)),
                    new Task("Test task2", "Test task2 description",
                            Duration.ofMinutes(10),
                            LocalDateTime.of(2025, 1, 1, 11, 30)),
                    new Task("Test task3", "Test task3 description",
                            Duration.ofMinutes(10),
                            LocalDateTime.of(2025, 1, 1, 11, 10))
            );

            for (Task task : taskList) {
                String taskJson = gson.toJson(task);

                // POST request
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/tasks"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            }

            // POST request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/prioritized"))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Tests
            List<Task> responsePrioritizedTasksList = gson.fromJson(response.body(), new TaskListTypeToken().getType());
            Task taskFirst = responsePrioritizedTasksList.get(0);
            Task taskASecond = responsePrioritizedTasksList.get(1);
            Task taskThird = responsePrioritizedTasksList.get(2);
            Stream<Executable> executables = Stream.of(
                    () -> assertNotNull(responsePrioritizedTasksList),
                    () -> assertEquals(3, responsePrioritizedTasksList.size()),
                    () -> assertEquals(taskList.get(2), taskFirst),
                    () -> assertEquals(taskList.get(1), taskASecond),
                    () -> assertEquals(taskList.get(0), taskThird)
            );

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
