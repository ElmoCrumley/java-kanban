package com.yandex.app.net;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.yandex.app.model.Task;
import com.yandex.app.service.TaskManager;

import java.io.IOException;
import java.util.ArrayList;

class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    TaskManager taskManager;

    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Gson gson = HttpTaskServer.getGson();
        ArrayList<Task> history = taskManager.getPrioritizedTasks();

        sendText(httpExchange, gson.toJson(history));
    }
}