package com.yandex.app.net;

import com.google.gson.*;
import com.sun.net.httpserver.HttpServer;
import com.yandex.app.service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    TaskManager taskManager;
    HttpServer httpServer;

    public HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public static void main(String[] args) throws IOException {

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