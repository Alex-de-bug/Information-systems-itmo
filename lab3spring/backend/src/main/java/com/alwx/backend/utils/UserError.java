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
    REQUEST_ALREADY_SEND("Вы уже отправили запрос на получение админских прав"),
    ADMIN_ALREAD_EXIST("Вы админ"),
    KILL_DEV("Убейте разработчика сайта"),
    USER_DOESNT_EXIST("Пользователя нет в бд"),
    USER_ALREADY_HAS_ADMIN("Пользователя уже является админом"),
    PASS_INVALID("Пароль должен быть более 2х символов и менее 21"),
    ENGINE_FUEL("Слишко маленький расход топлива при заданном объёме ваш минимальный расход: "),
    ENGINE_BICYCLE("Минимальная мощность двигателя велосипеда должна быть 350 Вт"),
    ENGINE_BOAT("Минимальная мощность двигателя лодки должна быть 2.5 л/с"),
    ENGINE_PLANE("Минимальная мощность двигателя самолёта должна быть 100 л/с");

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
