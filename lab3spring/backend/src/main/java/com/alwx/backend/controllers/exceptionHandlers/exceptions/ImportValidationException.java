package com.alwx.backend.controllers.exceptionHandlers.exceptions;


public class ImportValidationException extends RuntimeException {
    private final String token;

    public ImportValidationException(String message) {
        super(message);
        this.token = null;
    }

    public ImportValidationException(String message, String token) {
        super(message);
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}

