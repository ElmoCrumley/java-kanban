package com.yandex.app.service;

import java.io.IOException;

public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(IOException e) {
        System.out.println(e.getMessage());
    }
}
