package com.alwx.backend.controllers.exceptionHandlers;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.alwx.backend.controllers.exceptionHandlers.exceptions.BusinessValidationException;
import com.alwx.backend.dtos.AppError;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;


/**
 * Глобальный обработчик исключений для приложения. Обеспечивает
 * централизованную обработку исключений для всех методов с аннотацией
 * @RequestMapping.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(BusinessValidationException.class)
    public ResponseEntity<AppError> handleBusinessValidationException(BusinessValidationException ex, Locale locale) {
        return new ResponseEntity<>(
                new AppError(
                    HttpStatus.CONFLICT.value(), 
                    ex.getMessage()
                ), 
                HttpStatus.CONFLICT
            );
    }

    @ExceptionHandler(CannotAcquireLockException.class)
    public ResponseEntity<AppError> handleCannotAcquireLockException(CannotAcquireLockException ex, Locale locale) {
        return new ResponseEntity<>(
                new AppError(
                    HttpStatus.CONFLICT.value(), 
                    "Данный объект в настоящее время редактируется другим пользователем. Пожалуйста, повторите попытку позже."
                ), 
                HttpStatus.CONFLICT
            );
    }

    /**
     * Обрабатывает исключения, возникающие при невозможности чтения тела
     * запроса или при неверном формате данных.
     *
     * @param ex Исключение HttpMessageNotReadableException
     * @param locale Текущая локаль
     * @return ResponseEntity с деталями ошибки
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<AppError> handleFormatException(HttpMessageNotReadableException ex, Locale locale) {
        String fieldName = "";
        String message;

        if (ex.getCause() instanceof InvalidFormatException) {
            InvalidFormatException cause = (InvalidFormatException) ex.getCause();
            fieldName = cause.getPath().isEmpty() ? "" : cause.getPath().get(0).getFieldName();
            Class<?> targetType = cause.getTargetType();

            message = messageSource.getMessage(
                "typeMismatch.requestVehicle." + fieldName,
                new Object[]{fieldName, targetType.getSimpleName()},
                "Неверный формат поля '" + fieldName + "'. Значение должно быть типа '" + targetType.getSimpleName() + "'",
                locale
            );
        } else if (ex.getCause() instanceof JsonMappingException) {
            JsonMappingException cause = (JsonMappingException) ex.getCause();
            fieldName = cause.getPath().isEmpty() ? "" : cause.getPath().get(0).getFieldName();
            
            message = String.format("Ошибка в поле '%s': %s", 
                fieldName, 
                cause.getOriginalMessage());
        } else {
            message = messageSource.getMessage(
                "invalidFormat.generic",
                null,
                "Неверный формат данных",
                locale
            );
        }

        return ResponseEntity
            .badRequest()
            .body(new AppError(
                HttpStatus.BAD_REQUEST.value(),
                message
            ));
    }

    /**
     * Обрабатывает нарушения целостности базы данных 
     * @param ex Исключение DataIntegrityViolationException
     * @param locale Текущая локаль
     * @return ResponseEntity с деталями ошибки
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<AppError> handleDataIntegrityViolation(DataIntegrityViolationException ex, Locale locale) {
        System.out.println(ex);
        String message = messageSource.getMessage(
            "dataIntegrity.error",
            null,
            "Поле \"Название\" должно быть уникальным",
            locale
            );
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new AppError(HttpStatus.BAD_REQUEST.value(), message));
        
    }

    /**
     * @param ex Исключение MethodArgumentNotValidException
     * @param locale Текущая локаль
     * @return ResponseEntity с деталями ошибки
     */ 
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex, 
            Locale locale) {
        
        Map<String, String> errors = new HashMap<>();
        
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = messageSource.getMessage(error, locale);
            errors.put(fieldName, errorMessage);
        });
        
        return ResponseEntity
            .badRequest()
            .body(errors);
    }
}