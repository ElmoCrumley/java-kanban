package com.yandex.app.service;

public class NotFoundException extends RuntimeException {
    public NotFoundException() {
        super("Not Found");
    }
}
