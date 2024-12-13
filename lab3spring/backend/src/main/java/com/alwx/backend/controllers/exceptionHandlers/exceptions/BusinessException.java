package com.alwx.backend.controllers.exceptionHandlers.exceptions;

public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
