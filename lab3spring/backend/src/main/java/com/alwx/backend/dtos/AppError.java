package com.alwx.backend.dtos;
import java.util.Date;
import lombok.Data;



/**
 * Класс для шаблонных ошибок-ответов клиенту
 */
@Data
public class AppError {
    private int status;
    private String message;
    private Date timestamp;

    /**
     * Конструктор для создания объекта AppError.
     * @param status Статус HTTP-ответа
     * @param message Сообщение об ошибке
     */
    public AppError(int status, String message){
        this.status = status;
        this.message = message;
        this.timestamp = new Date();
    }
}
