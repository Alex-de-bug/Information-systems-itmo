package com.alwx.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;


/**
 * DTO для передачи данных о пользователе после регистрации
 */
@Data
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String username;
}
