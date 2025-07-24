package com.yandex.app.net;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.yandex.app.model.Task;
import com.yandex.app.service.NotFoundException;
import com.yandex.app.service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

class TasksHandler extends BaseHttpHandler {
    TaskManager taskManager;

    public TasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            String path = httpExchange.getRequestURI().getPath();
            String id = null;

            if (path.split("/").length == 3) {
                id = path.split("/")[2];
            }

            Gson gson = HttpTaskServer.getGson();

            switch (httpExchange.getRequestMethod()) {
                case "GET":
                    if (id != null) {
                        Task task = taskManager.getTask(Integer.parseInt(id));
                        System.out.println(
                                "Выполнена передача задачи: \n" + gson.toJson(task)
                        );
                        sendText(httpExchange, gson.toJson(task));
                    } else {
                        ArrayList<Task> tasksList = taskManager.getTasksList();
                        System.out.println("Выполнена передача списка задач:");
                        for (Task task : tasksList) {
                            System.out.println("\"name\": \"" + task.getName() + "\", "
                                    + "\"description\": \"" + task.getDescription() + "\";");
                        }
                        sendText(httpExchange, gson.toJson(tasksList));
                    }
                    break;
                case "POST":
                    // body
                    InputStream inputStream = httpExchange.getRequestBody();
                    String jsonTask = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    Task task = gson.fromJson(jsonTask, Task.class);

                    if (id != null) {
                        taskManager.updateTask(task);
                        System.out.println(
                                "Выполнено обновление задачи: \n" + gson.toJson(task)
                        );
                        sendText(httpExchange, 201);
                    } else {
                        taskManager.createTask(task);
                        System.out.println(
                                "Выполнено создание задачи: \n" + gson.toJson(task)
                        );
                        sendText(httpExchange, 201);
                    }
                    break;
                case "DELETE":
                    if (id != null) {
                        System.out.println(
                                "Выполнено удаление задачи: \n"
                                        + gson.toJson(taskManager.getTask(Integer.parseInt(id)))
                        );
                        taskManager.removeTask(Integer.parseInt(id));
                        sendText(httpExchange, 200);
                    }
                    break;
                default:
                    sendHasOverlaps(httpExchange);
            }
        } catch (NotFoundException e) {
            sendNotFound(httpExchange);
        } catch (RuntimeException e) {
            sendHasOverlaps(httpExchange);
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }
}