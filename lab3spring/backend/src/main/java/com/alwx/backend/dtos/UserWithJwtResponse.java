package com.alwx.backend.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;


/**
 * DTO для передачи токена на запрос аутентификации
 */
@Data
@AllArgsConstructor
public class UserWithJwtResponse {
    private String name;
    private List<String> roles;
    private String token;
}
