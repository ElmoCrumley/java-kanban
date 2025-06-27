package com.yandex.app;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.yandex.app.service.Managers;
import com.yandex.app.service.TaskManager;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {

    public static void main(String[] args) throws IOException {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(8080), 0);

        httpServer.createContext("/tasks", new TasksHandler());
        httpServer.createContext("/subtasks", new SubtasksHandler());
        httpServer.createContext("/epics", new EpicsHandler());
        httpServer.createContext("/history", new HistoryHandler());
        httpServer.createContext("/prioritized", new PrioritizedHandler());

        File log = File.createTempFile("myTempFile", ".txt");

        TaskManager taskManager = Managers.getDefault(log);
    }
}

class TasksHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

    }
}

class SubtasksHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

    }
}

class EpicsHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

    }
}

class HistoryHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

    }
}

class PrioritizedHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

    }
}