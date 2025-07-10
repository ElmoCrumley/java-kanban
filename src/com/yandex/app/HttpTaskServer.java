package com.yandex.app;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.yandex.app.model.Task;
import com.yandex.app.model.Epic;
import com.yandex.app.model.SubTask;
import com.yandex.app.service.InMemoryTaskManager;
import com.yandex.app.service.Managers;
import com.yandex.app.service.TaskManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

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

class TasksHandler extends BaseHttpHandler implements HttpHandler {
    TaskManager taskManager;

    TasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        String id = path.split("/")[2];
        Gson gson = new Gson();

        switch(httpExchange.getRequestMethod()) {
            case "GET":
                try {
                    if (id != null) {
                        Task task = taskManager.getTask(Integer.parseInt(id));
                        sendText(httpExchange, gson.toJson(task));
                    } else {
                        ArrayList<Task> tasksList = taskManager.getTasksList();
                        sendText(httpExchange, gson.toJson(tasksList));
                    }
                } catch (RuntimeException e) {
                    sendNotFound(httpExchange);
                }
            case "POST":
                // body
                InputStream inputStream = httpExchange.getRequestBody();
                String jsonTask = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                Task task = gson.fromJson(jsonTask, Task.class);

                try {
                    if (id != null) {
                        taskManager.updateTask(task);
                        sendText(httpExchange, 201);
                    } else {
                        taskManager.createTask(task);
                        sendText(httpExchange, 201);
                    }
                } catch (RuntimeException e) {
                    sendHasOverlaps(httpExchange);
                }
            case "DELETE":
                if (id != null) {
                    taskManager.removeTask(Integer.parseInt(id));
                    sendText(httpExchange, 200);
                }
            default:
                break;
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

        switch(httpExchange.getRequestMethod()) {
            case "GET":
                try {
                    if (id != null) {
                        SubTask subTask = taskManager.getSubTask(Integer.parseInt(id));
                        sendText(httpExchange, gson.toJson(subTask));
                    } else {
                        ArrayList<SubTask> subTasksList = taskManager.getSubTasksList();
                        sendText(httpExchange, gson.toJson(subTasksList));
                    }
                } catch (RuntimeException e) {
                    sendNotFound(httpExchange);
                }
            case "POST":
                // body
                InputStream inputStream = httpExchange.getRequestBody();
                String jsonTask = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                SubTask subTask = gson.fromJson(jsonTask, SubTask.class);
                String epicsId = path.split("/")[3];

                try {
                    if (id != null) {
                        taskManager.updateSubTask(subTask);
                        sendText(httpExchange, 201);
                    } else {
                        taskManager.createSubTask(subTask, Integer.parseInt(epicsId));
                        sendText(httpExchange, 201);
                    }
                } catch (RuntimeException e) {
                    sendHasOverlaps(httpExchange);
                }
            case "DELETE":
                if (id != null) {
                    taskManager.removeSubTask(Integer.parseInt(id));
                    sendText(httpExchange, 200);
                }
            default:
                break;
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

        switch(httpExchange.getRequestMethod()) {
            case "GET":
                try {
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
                } catch (RuntimeException e) {
                    sendNotFound(httpExchange);
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