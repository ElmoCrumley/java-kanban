package com.yandex.app.net;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.yandex.app.model.Epic;
import com.yandex.app.model.SubTask;
import com.yandex.app.service.InMemoryTaskManager;
import com.yandex.app.service.NotFoundException;
import com.yandex.app.service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

class EpicsHandler extends BaseHttpHandler {
    TaskManager taskManager;

    public EpicsHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            String path = httpExchange.getRequestURI().getPath();
            String id = null;
            String subtasks = null;

            if (path.split("/").length == 3) {
                id = path.split("/")[2];
            } else if (path.split("/").length == 4) {
                id = path.split("/")[2];
                subtasks = path.split("/")[3];
            }

            Gson gson = HttpTaskServer.getGson();

            switch (httpExchange.getRequestMethod()) {
                case "GET":
                    if (id != null && subtasks != null) {
                        Epic epic = taskManager.getEpic(Integer.parseInt(id));
                        ArrayList<SubTask> subTasksList = InMemoryTaskManager.getEpicsSubTasksList(epic);
                        System.out.println("Выполнена передача списка подзадач эпика " + epic.getId() + ':');
                        for (SubTask subTask : subTasksList) {
                            System.out.println("\"name\": \"" + subTask.getName() + "\", "
                                    + "\"description\": \"" + subTask.getDescription() + "\";");
                        }
                        sendText(httpExchange, gson.toJson(subTasksList));
                    } else if (id == null && subtasks == null) {
                        ArrayList<Epic> epicsList = taskManager.getEpicsList();
                        System.out.println("Выполнена передача списка эпиков:");
                        for (Epic epic : epicsList) {
                            System.out.println("\"name\": \"" + epic.getName() + "\", "
                                    + "\"description\": \"" + epic.getDescription() + "\";");
                        }
                        sendText(httpExchange, gson.toJson(epicsList));
                    } else if (id != null) {
                        Epic epic = taskManager.getEpic(Integer.parseInt(id));
                        System.out.println(
                                "Выполнена передача эпика: \n" + gson.toJson(epic)
                        );
                        sendText(httpExchange, gson.toJson(epic));
                    }
                    break;
                case "POST":
                    // body
                    InputStream inputStream = httpExchange.getRequestBody();
                    String jsonTask = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    Epic epic = gson.fromJson(jsonTask, Epic.class);

                    taskManager.createEpic(epic);
                    System.out.println(
                            "Выполнено создание эпика: \n" + gson.toJson(epic)
                    );
                    sendText(httpExchange, 201);
                    break;
                case "DELETE":
                    if (id != null) {
                        taskManager.removeEpic(Integer.parseInt(id));
                        System.out.println(
                                "Выполнено удаление подзадачи: \n"
                                        + gson.toJson(taskManager.getEpic(Integer.parseInt(id)))
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