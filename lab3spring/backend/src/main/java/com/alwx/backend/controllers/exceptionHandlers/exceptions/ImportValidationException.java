package com.alwx.backend.controllers.exceptionHandlers.exceptions;


public class ImportValidationException extends RuntimeException {
    public ImportValidationException(String message) {
        super(message);
    }
}
