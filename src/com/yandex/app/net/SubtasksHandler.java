package com.yandex.app.net;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.yandex.app.model.SubTask;
import com.yandex.app.service.NotFoundException;
import com.yandex.app.service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

class SubtasksHandler extends BaseHttpHandler {
    TaskManager taskManager;

    public SubtasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            String path = httpExchange.getRequestURI().getPath();
            String id = null;

            if (path.split("/").length >= 3) {
                id = path.split("/")[2];
            }

            Gson gson = HttpTaskServer.getGson();

            switch (httpExchange.getRequestMethod()) {
                case "GET":
                    if (id != null) {
                        SubTask subTask = taskManager.getSubTask(Integer.parseInt(id));
                        System.out.println(
                                "Выполнена передача подзадачи: \n" + gson.toJson(subTask)
                        );
                        sendText(httpExchange, gson.toJson(subTask));
                    } else {
                        ArrayList<SubTask> subTasksList = taskManager.getSubTasksList();
                        System.out.println("Выполнена передача списка подзадач:");
                        for (SubTask subTask : subTasksList) {
                            System.out.println("\"name\": \"" + subTask.getName() + "\", "
                                    + "\"description\": \"" + subTask.getDescription() + "\";");
                        }
                        sendText(httpExchange, gson.toJson(subTasksList));
                    }
                    break;
                case "POST":
                    // body
                    InputStream inputStream = httpExchange.getRequestBody();
                    String jsonTask = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    SubTask subTask = gson.fromJson(jsonTask, SubTask.class);

                    if (subTask.getId() != 0) {
                        taskManager.updateSubTask(subTask);
                        System.out.println(
                                "Выполнено обновление подзадачи: \n" + gson.toJson(subTask)
                        );
                        sendText(httpExchange, 201);
                    } else {
                        taskManager.createSubTask(subTask, subTask.getEpicsId());
                        System.out.println(
                                "Выполнено создание подзадачи: \n" + gson.toJson(subTask)
                        );
                        sendText(httpExchange, 201);
                    }
                    break;
                case "DELETE":
                    if (id != null) {
                        taskManager.removeSubTask(Integer.parseInt(id));
                        System.out.println(
                                "Выполнено удаление подзадачи: \n"
                                        + gson.toJson(taskManager.getSubTask(Integer.parseInt(id)))
                        );
                        sendText(httpExchange, 200);
                    }
                    break;
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