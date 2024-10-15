package com.alwx.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegUserDto {
    private Long id;
    private String username;
    private String password;
    private String confirmPassword;
}
