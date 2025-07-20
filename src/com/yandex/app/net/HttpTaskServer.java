package com.yandex.app.net;

import com.google.gson.*;
import com.sun.net.httpserver.HttpServer;
import com.yandex.app.model.Task;
import com.yandex.app.service.InMemoryTaskManager;
import com.yandex.app.service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    TaskManager taskManager;
    HttpServer httpServer;

    public HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public static void main(String[] args) throws IOException {
        try {
            TaskManager taskManager = new InMemoryTaskManager();
            HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);

            httpTaskServer.start();
//            HttpClient client = HttpClient.newHttpClient();
//
//            // Тестовый запрос
//            HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create("http://localhost:8080/hello"))
//                .build();
//
//            HttpResponse<String> response1 = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//            // Post запрос
//            Gson gson = HttpTaskServer.getGson();
//            Task task1 = new Task("Name", "Description", Duration.ofMinutes(5), LocalDateTime.now());
//            String taskJson = gson.toJson(task1);
//            HttpRequest request = HttpRequest.newBuilder()
//                    .uri(URI.create("http://localhost:8080/tasks"))
//                    .header("Content-Type", "application/json")
//                    .POST(HttpRequest.BodyPublishers.ofString(taskJson))
//                    .build();
//
//            HttpResponse<String> response2 = client.send(request, HttpResponse.BodyHandlers.ofString());
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
        httpServer.createContext("/tasks", new TasksHandler(this.taskManager));
        httpServer.createContext("/subtasks", new SubtasksHandler(this.taskManager));
        httpServer.createContext("/epics", new EpicsHandler(this.taskManager));
        httpServer.createContext("/history", new HistoryHandler(this.taskManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(this.taskManager));
        httpServer.createContext("/hello", new HelloHandler());
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
    }

    public static Gson getGson() {
        return new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }
}