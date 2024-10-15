package com.alwx.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;


/**
 * DTO для передачи токена на запрос аутентификации
 */
@Data
@AllArgsConstructor
public class JwtResponse {
    private String token;
}
