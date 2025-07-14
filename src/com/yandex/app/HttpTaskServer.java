package com.yandex.app;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.yandex.app.model.Task;
import com.yandex.app.model.Epic;
import com.yandex.app.model.SubTask;
import com.yandex.app.service.InMemoryTaskManager;
import com.yandex.app.service.NotFoundException;
import com.yandex.app.service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HttpTaskServer {
    TaskManager taskManager;
    HttpServer httpServer;

    public HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public static void main(String[] args) throws IOException {
        TaskManager taskManager = new InMemoryTaskManager();
        HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();
    }

    public void start() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
        httpServer.createContext("/tasks", new TasksHandler(this.taskManager));
        httpServer.createContext("/subtasks", new SubtasksHandler(this.taskManager));
        httpServer.createContext("/epics", new EpicsHandler(this.taskManager));
        httpServer.createContext("/history", new HistoryHandler(this.taskManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(this.taskManager));
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
    }

    public static Gson getGson() {
        return new Gson();
    }
}

class TasksHandler extends BaseHttpHandler implements HttpHandler {
    TaskManager taskManager;

    public TasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        String id = path.split("/")[2];
        Gson gson = new Gson();

        try {
            switch(httpExchange.getRequestMethod()) {
                case "GET":
                    if (id != null) {
                        Task task = taskManager.getTask(Integer.parseInt(id));
                        sendText(httpExchange, gson.toJson(task));
                    } else {
                        ArrayList<Task> tasksList = taskManager.getTasksList();
                        sendText(httpExchange, gson.toJson(tasksList));
                    }
                case "POST":
                    // body
                    InputStream inputStream = httpExchange.getRequestBody();
                    String jsonTask = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    Task task = gson.fromJson(jsonTask, Task.class);

                    if (id != null) {
                        taskManager.updateTask(task);
                        sendText(httpExchange, 201);
                    } else {
                        taskManager.createTask(task);
                        sendText(httpExchange, 201);
                    }
                case "DELETE":
                    if (id != null) {
                        taskManager.removeTask(Integer.parseInt(id));
                        sendText(httpExchange, 200);
                    }
                default:
                    break;
            }
        } catch (NotFoundException ignored) {
            sendNotFound(httpExchange);
        } catch (RuntimeException ignored) {
            sendHasOverlaps(httpExchange);
        }
    }
}

class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    TaskManager taskManager;

    public SubtasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        String id = path.split("/")[2];
        Gson gson = new Gson();

        try {
            switch(httpExchange.getRequestMethod()) {
                case "GET":
                    if (id != null) {
                        SubTask subTask = taskManager.getSubTask(Integer.parseInt(id));
                        sendText(httpExchange, gson.toJson(subTask));
                    } else {
                        ArrayList<SubTask> subTasksList = taskManager.getSubTasksList();
                        sendText(httpExchange, gson.toJson(subTasksList));
                    }
                case "POST":
                    // body
                    InputStream inputStream = httpExchange.getRequestBody();
                    String jsonTask = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    SubTask subTask = gson.fromJson(jsonTask, SubTask.class);
                    String epicsId = path.split("/")[3];

                    if (id != null) {
                        taskManager.updateSubTask(subTask);
                        sendText(httpExchange, 201);
                    } else {
                        taskManager.createSubTask(subTask, Integer.parseInt(epicsId));
                        sendText(httpExchange, 201);
                    }
                case "DELETE":
                    if (id != null) {
                        taskManager.removeSubTask(Integer.parseInt(id));
                        sendText(httpExchange, 200);
                    }
                default:
                    break;
            }
        } catch (NotFoundException ignored) {
            sendNotFound(httpExchange);
        } catch (RuntimeException ignored) {
            sendHasOverlaps(httpExchange);
        }
    }
}

class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    TaskManager taskManager;

    public EpicsHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        String id = path.split("/")[2];
        String subtasks = path.split("/")[3];

        Gson gson = new Gson();

        try {
            switch(httpExchange.getRequestMethod()) {
                case "GET":
                    if (id != null && subtasks != null) {
                        Epic epic = taskManager.getEpic(Integer.parseInt(id));
                        ArrayList<SubTask> subTasksList = InMemoryTaskManager.getEpicsSubTasksList(epic);
                        sendText(httpExchange, gson.toJson(subTasksList));
                    } else if (id == null && subtasks == null) {
                        ArrayList<Epic> epicsList = taskManager.getEpicsList();
                        sendText(httpExchange, gson.toJson(epicsList));
                    } else if (id != null) {
                        Epic epic = taskManager.getEpic(Integer.parseInt(id));
                        sendText(httpExchange, gson.toJson(epic));
                    }
                case "POST":
                    // body
                    InputStream inputStream = httpExchange.getRequestBody();
                    String jsonTask = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    Epic epic = gson.fromJson(jsonTask, Epic.class);

                    taskManager.createEpic(epic);
                    sendText(httpExchange, 201);
                case "DELETE":
                    if (id != null) {
                        taskManager.removeSubTask(Integer.parseInt(id));
                        sendText(httpExchange, 200);
                    }
                default:
                    break;
            }
        } catch (NotFoundException ignored) {
            sendNotFound(httpExchange);
        } catch (RuntimeException ignored) {
            sendHasOverlaps(httpExchange);
        }
    }
}

class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    TaskManager taskManager;

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Gson gson = new Gson();
        List<Task> history = taskManager.getHistoryManager().getHistory();

        sendText(httpExchange, gson.toJson(history));
    }
}

class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    TaskManager taskManager;

    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Gson gson = new Gson();
        ArrayList<Task> history = taskManager.getPrioritizedTasks();

        sendText(httpExchange, gson.toJson(history));
    }
}

class BaseHttpHandler {
    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendText(HttpExchange h, int code) throws IOException {
        h.sendResponseHeaders(code, 0);
        h.close();
    }

    protected void sendNotFound(HttpExchange h) throws IOException {
        byte[] resp = "Not Found".getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(404, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendHasOverlaps(HttpExchange h) throws IOException {
        byte[] resp = "Not Acceptable".getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(406, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }
}