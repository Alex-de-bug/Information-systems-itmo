package com.alwx.backend.utils;


/**
 * Перечисление, содержащее их описания.
 */
public enum UserError {
    USER_NOT_FOUND("Пользователь не найден"),
    PASSWORDS_DO_NOT_MATCH("Пароли не совпадают"),
    USER_ALREADY_EXISTS("Пользователь с таким именем уже существует"),
    BAD_CREDENTIALS("Неправильный логин или пароль"),
    TOKEN_EXPIRED("Время жизни токена истекло"),
    TOKEN_INVALID("Неверная подпись токена"),
    LOGIN_INVALID("Логин должен быть более 2х символов и менее 21"),
    PASS_INVALID("Пароль должен быть более 2х символов и менее 21");

    private final String message;

    /**
     * Конструктор для инициализации сообщения ошибки.
     *
     * @param message сообщение об ошибке
     */
    UserError(String message){
        this.message = message;
    }

    /**
     * Возвращает сообщение об ошибке.
     *
     * @return сообщение об ошибке
     */
    public String getMessage() {
        return message;
    }
}
