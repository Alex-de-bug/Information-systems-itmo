package com.alwx.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;


/**
 * DTO для передачи данных о новом пользователе при регистрации (получение от пользователя)
 */
@Data
@AllArgsConstructor
public class RegUserDto {
    private String username;
    private String password;
    private String confirmPassword;
}
