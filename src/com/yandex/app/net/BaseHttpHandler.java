package com.yandex.app.net;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

class BaseHttpHandler {
    protected void sendText(HttpExchange h, String text) throws IOException {
        try {
            byte[] resp = text.getBytes(); //StandardCharsets.UTF_8
            h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            h.sendResponseHeaders(200, resp.length);
            System.out.println("Запрос /" + h.getRequestURI().toString().split("/")[1] + ", код 200" + "\n");
            h.getResponseBody().write(resp);
            h.close();
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }

    protected void sendText(HttpExchange h, int code) throws IOException {
        System.out.println("Запрос /" + h.getRequestURI().toString().split("/")[1] + ", код " + code + "\n");
        h.sendResponseHeaders(code, 0);
        h.close();
    }

    protected void sendNotFound(HttpExchange h) throws IOException {
        try {
            byte[] resp = "Not Found".getBytes(); //StandardCharsets.UTF_8
            h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            h.sendResponseHeaders(404, resp.length);
            System.out.println("Запрос /" + h.getRequestURI().toString().split("/")[1] + ", код 404" + "\n");
            h.getResponseBody().write(resp);
            h.close();
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }

    protected void sendHasOverlaps(HttpExchange h) throws IOException {
        try {
            byte[] resp = "Not Acceptable".getBytes(); //StandardCharsets.UTF_8
            h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            h.sendResponseHeaders(406, resp.length);
            System.out.println("Запрос /" + h.getRequestURI().toString().split("/")[1] + ", код 406" + "\n");
            h.getResponseBody().write(resp);
            h.close();
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }
}