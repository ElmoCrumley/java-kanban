package com.yandex.app.net;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.yandex.app.model.SubTask;
import com.yandex.app.service.NotFoundException;
import com.yandex.app.service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    TaskManager taskManager;

    public SubtasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        String id = path.split("/")[2];
        Gson gson = HttpTaskServer.getGson();

        try {
            switch (httpExchange.getRequestMethod()) {
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