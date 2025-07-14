package com.yandex.app;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.yandex.app.model.Epic;
import com.yandex.app.model.SubTask;
import com.yandex.app.service.InMemoryTaskManager;
import com.yandex.app.service.NotFoundException;
import com.yandex.app.service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

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
            switch (httpExchange.getRequestMethod()) {
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