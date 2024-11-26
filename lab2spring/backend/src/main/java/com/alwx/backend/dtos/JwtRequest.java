package com.alwx.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO для передачи данных запроса на аутентификацию
 */
@Data 
@AllArgsConstructor 
public class JwtRequest {
    private String username; 
    private String password; 
}
