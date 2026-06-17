package com.example.data.exception;

public class SessionExistsException extends RuntimeException {
    public SessionExistsException(String message) {
        super(message);
    }
}
