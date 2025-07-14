package com.yandex.app;

import com.google.gson.*;
import com.sun.net.httpserver.HttpServer;
import com.yandex.app.service.InMemoryTaskManager;
import com.yandex.app.service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

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