package com.yandex.app;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.yandex.app.model.Task;
import com.yandex.app.service.NotFoundException;
import com.yandex.app.service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

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
            switch (httpExchange.getRequestMethod()) {
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