package com.yandex.app.net;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

abstract class BaseHttpHandler implements HttpHandler {
    String header = "Content-Type";
    String value = "application/json;charset=utf-8";
    String requestRus = "Запрос ";

    protected void sendText(HttpExchange h, String text) throws IOException {
        try {
            byte[] resp = text.getBytes(); //StandardCharsets.UTF_8
            h.getResponseHeaders().add(header, value);
            h.sendResponseHeaders(200, resp.length);
            System.out.println(requestRus
                    + h.getRequestMethod() + ' '
                    + h.getRequestURI().toString()
                    + ", код 200" + "\n");
            h.getResponseBody().write(resp);
            h.close();
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }

    protected void sendText(HttpExchange h, int code) throws IOException {
        System.out.println(requestRus
                + h.getRequestMethod() + ' '
                + h.getRequestURI().toString() + ", код " + code + "\n");
        h.sendResponseHeaders(code, 0);
        h.close();
    }

    protected void sendNotFound(HttpExchange h) throws IOException {
        try {
            byte[] resp = "Not Found".getBytes(); //StandardCharsets.UTF_8
            h.getResponseHeaders().add(header, value);
            h.sendResponseHeaders(404, resp.length);
            System.out.println(requestRus
                    + h.getRequestMethod() + ' '
                    + h.getRequestURI().toString() + ", код 404" + "\n");
            h.getResponseBody().write(resp);
            h.close();
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }

    protected void notAllowed(HttpExchange h) throws IOException {
        try {
            byte[] resp = "Not allowed".getBytes(); //StandardCharsets.UTF_8
            h.getResponseHeaders().add(header, value);
            h.sendResponseHeaders(405, resp.length);
            System.out.println(requestRus
                    + h.getRequestMethod() + ' '
                    + h.getRequestURI().toString() + ", код 405" + "\n");
            h.getResponseBody().write(resp);
            h.close();
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }

    protected void sendHasOverlaps(HttpExchange h) throws IOException {
        try {
            byte[] resp = "Not Acceptable".getBytes(); //StandardCharsets.UTF_8
            h.getResponseHeaders().add(header, value);
            h.sendResponseHeaders(406, resp.length);
            System.out.println(requestRus
                    + h.getRequestMethod() + ' '
                    + h.getRequestURI().toString() + ", код 406" + "\n");
            h.getResponseBody().write(resp);
            h.close();
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }
}