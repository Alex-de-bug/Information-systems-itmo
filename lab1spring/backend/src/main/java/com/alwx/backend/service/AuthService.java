package com.alwx.backend.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.alwx.backend.dtos.JwtRequest;
import com.alwx.backend.dtos.JwtResponse;
import com.alwx.backend.dtos.RegUserDto;
import com.alwx.backend.dtos.UserDto;
import com.alwx.backend.exceptions.AppError;
import com.alwx.backend.models.User;
import com.alwx.backend.utils.JwtTokenUtil;

import lombok.RequiredArgsConstructor;


/**
 * Сервис для аутентификации пользователей и управления регистрацией.
 */
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtils;
    private final AuthenticationManager authenticationManager;

    /**
     * Создает токен аутентификации для пользователя
     *
     * @param authRequest объект с данными для аутентификации (логин и пароль)
     * @return ResponseEntity с токеном аутентификации или ошибкой
     */
    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest authRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), "Неправильный логин или пароль"), HttpStatus.UNAUTHORIZED);
        }
        UserDetails userDetails = userService.loadUserByUsername(authRequest.getUsername());
        String token = jwtTokenUtils.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(token));
    }

    /**
     * Регистрирует нового пользователя
     *
     * @param registrationUserDto объект с данными нового пользователя
     * @return ResponseEntity с информацией о новом пользователе или ошибкой
     */
    public ResponseEntity<?> createNewUser(@RequestBody RegUserDto registrationUserDto) {
        if (!registrationUserDto.getPassword().equals(registrationUserDto.getConfirmPassword())) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Пароли не совпадают"), HttpStatus.BAD_REQUEST);
        }
        if (userService.findByUsername(registrationUserDto.getUsername()).isPresent()) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Пользователь с указанным именем уже существует"), HttpStatus.BAD_REQUEST);
        }
        User user = userService.createNewUser(registrationUserDto);
        return ResponseEntity.ok(new UserDto(user.getId(), user.getUsername()));
    }
}
