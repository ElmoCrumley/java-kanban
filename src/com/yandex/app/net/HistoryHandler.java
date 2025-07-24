package com.yandex.app.net;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.yandex.app.model.Task;
import com.yandex.app.service.NotFoundException;
import com.yandex.app.service.TaskManager;

import java.io.IOException;
import java.util.List;

class HistoryHandler extends BaseHttpHandler {
    TaskManager taskManager;

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        switch (httpExchange.getRequestMethod()) {
            case "GET":
                try {
                    Gson gson = HttpTaskServer.getGson();
                    List<Task> history = taskManager.getHistoryManager().getHistory();
                    System.out.println("Выполнена передача истории:");
                    for (Task task : history) {
                        System.out.println("\"name\": \"" + task.getName() + "\", "
                                + "\"description\": \"" + task.getDescription() + "\";");
                    }
                    sendText(httpExchange, gson.toJson(history));
                } catch (NotFoundException e) {
                    sendNotFound(httpExchange);
                } catch (RuntimeException e) {
                    sendHasOverlaps(httpExchange);
                } catch (Exception e) {
                    System.out.println(e.getStackTrace());
                }
                break;
            default:
                sendHasOverlaps(httpExchange);
        }
    }
}