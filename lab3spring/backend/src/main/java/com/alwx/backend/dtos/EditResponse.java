package com.alwx.backend.dtos;

import lombok.Data;

/**
 * DTO для ответа администратора.
 */
@Data
public class EditResponse {
    private String username;
    /**
     * Флаг, указывающий, одобрен ли запрос.
     */
    private Boolean rulling;
}
