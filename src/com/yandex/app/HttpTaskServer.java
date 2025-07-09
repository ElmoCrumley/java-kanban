package com.yandex.app;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.yandex.app.model.Task;
import com.yandex.app.service.Managers;
import com.yandex.app.service.TaskManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class HttpTaskServer {
    public static void main(String[] args) throws IOException {
        File log = File.createTempFile("myTempFile", ".txt");
        TaskManager taskManager = Managers.getDefault(log);

        HttpServer httpServer = HttpServer.create(new InetSocketAddress(8080), 0);

        httpServer.createContext("/tasks", new TasksHandler(taskManager));
        httpServer.createContext("/subtasks", new SubtasksHandler(taskManager));
        httpServer.createContext("/epics", new EpicsHandler(taskManager));
        httpServer.createContext("/history", new HistoryHandler(taskManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager));
        httpServer.start();


    }
}

class TasksHandler implements HttpHandler {
    TaskManager taskManager;

    TasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String response;

        switch(httpExchange.getRequestMethod()) {
            case "GET":
                response = handleGetRequest(httpExchange);
            case "POST":
                response = handlePostRequest(httpExchange);
            case "DELETE":
                response = handleDeleteRequest(httpExchange);
            default:
                response = "Некорректный метод!";
        }

        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    String handleGetRequest(HttpExchange httpExchange) throws IOException {
        // path
        String path = httpExchange.getRequestURI().getPath();
        String id = path.split("/")[2];
        // for serialization
        Gson gson = new Gson();

        try {
            if (id != null) {
                Task task = taskManager.getTask(Integer.parseInt(id));
                httpExchange.sendResponseHeaders(200, 0);
                return gson.toJson(task);
            } else {
                ArrayList<Task> tasksList = taskManager.getTasksList();
                httpExchange.sendResponseHeaders(200, 0);
                return gson.toJson(tasksList);
            }
        } catch (RuntimeException e) {
            httpExchange.sendResponseHeaders(404, 0);
            return "Not Found";
        }
    }

    String handlePostRequest(HttpExchange httpExchange) throws IOException {
        // path
        String path = httpExchange.getRequestURI().getPath();
        String id = path.split("/")[2];
        // body
        InputStream inputStream = httpExchange.getRequestBody();
        String jsonTask = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        // deserialization
        Gson gson = new Gson();
        Task task = gson.fromJson(jsonTask, Task.class);

        try {
            if (id != null) {
                taskManager.updateTask(task);
                httpExchange.sendResponseHeaders(201, 0);
                return "";
            } else {
                taskManager.createTask(task);
                httpExchange.sendResponseHeaders(201, 0);
                return "";
            }
        } catch (RuntimeException e) {
            httpExchange.sendResponseHeaders(406, 0);
            return "Not Acceptable";
        }
    }

    String handleDeleteRequest(HttpExchange httpExchange) throws IOException {
        // path
        String path = httpExchange.getRequestURI().getPath();
        String id = path.split("/")[2];
        // for serialization
        Gson gson = new Gson();

        taskManager.removeTask(Integer.parseInt(id));
        httpExchange.sendResponseHeaders(200, 0);
        return "";
    }
}

class SubtasksHandler implements HttpHandler {
    TaskManager taskManager;

    public SubtasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

    }
}

class EpicsHandler implements HttpHandler {
    TaskManager taskManager;

    public EpicsHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

    }
}

class HistoryHandler implements HttpHandler {
    TaskManager taskManager;

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

    }
}

class PrioritizedHandler implements HttpHandler {
    TaskManager taskManager;

    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

    }
}