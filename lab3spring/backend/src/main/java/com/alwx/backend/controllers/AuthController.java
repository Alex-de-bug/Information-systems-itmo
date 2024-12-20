package com.alwx.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alwx.backend.dtos.JwtRequest;
import com.alwx.backend.dtos.RegUserDto;
import com.alwx.backend.service.AuthService;

/**
 * Контроллер для обработки аутентификации и регистрации пользователей
 */
@RestController
@RequestMapping("/api")
public class AuthController {

    /**
     * Сервис для работы с аутентификацией и регистрацией пользователей.
     */
    @Autowired
    private AuthService authService;

    /**
     * Создает токен аутентификации на основе данных пользователя
     *
     * @param jwtRequest объект с данными для аутентификации (логин и пароль)
     * @return ResponseEntity с токеном аутентификации, логином, ролями пользователя
     */
    @PostMapping("/auth")
    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest jwtRequest) {
        return authService.createAuthToken(jwtRequest);
    }

    /**
     * Регистрирует нового пользователя.
     *
     * @param regUserDto объект с данными нового пользователя
     * @return ResponseEntity с информацией о результате регистрации
     */
    @PostMapping("/reg")
    public ResponseEntity<?> createNewUser(@RequestBody RegUserDto regUserDto) {
        return authService.createNewUser(regUserDto);
    }
}
