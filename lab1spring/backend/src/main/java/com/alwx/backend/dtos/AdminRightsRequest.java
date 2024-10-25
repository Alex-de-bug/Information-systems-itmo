package com.alwx.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO для запроса прав администратора.
 */
@Data
@AllArgsConstructor
public class AdminRightsRequest {
    private String username;
}
