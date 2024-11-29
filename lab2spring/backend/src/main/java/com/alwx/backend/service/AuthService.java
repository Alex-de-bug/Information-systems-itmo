package com.alwx.backend.service;


import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import com.alwx.backend.dtos.AppError;
import com.alwx.backend.dtos.JwtRequest;
import com.alwx.backend.dtos.UserWithJwtResponse;
import com.alwx.backend.dtos.RegUserDto;
import com.alwx.backend.dtos.UserDto;
import com.alwx.backend.models.User;
import com.alwx.backend.utils.UserError;
import com.alwx.backend.utils.jwt.JwtTokenUtil;

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
            return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), UserError.BAD_CREDENTIALS.getMessage()), HttpStatus.UNAUTHORIZED);
        }
        UserDetails userDetails = userService.loadUserByUsername(authRequest.getUsername());
        String token = jwtTokenUtils.generateToken(userDetails);
        return ResponseEntity.ok(
            new UserWithJwtResponse(
                authRequest.getUsername(), 
                userService.loadRolesByUsername(authRequest.getUsername()).stream().map(Object::toString).collect(Collectors.toList()), 
                token));
    }

    /**
     * Регистрирует нового пользователя
     *
     * @param registrationUserDto объект с данными нового пользователя
     * @return ResponseEntity с информацией о новом пользователе или ошибкой
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ResponseEntity<?> createNewUser(@RequestBody RegUserDto registrationUserDto) {
        if (!registrationUserDto.getPassword().equals(registrationUserDto.getConfirmPassword())) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), UserError.PASSWORDS_DO_NOT_MATCH.getMessage()), HttpStatus.BAD_REQUEST);
        }
        if (userService.findByUsername(registrationUserDto.getUsername()).isPresent()) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), UserError.USER_ALREADY_EXISTS.getMessage()), HttpStatus.BAD_REQUEST);
        }
        if (registrationUserDto.getUsername().length() < 3 || registrationUserDto.getUsername().length() > 20) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), UserError.LOGIN_INVALID.getMessage()), HttpStatus.BAD_REQUEST);
        }
        if (registrationUserDto.getPassword().length() < 3 || registrationUserDto.getPassword().length() > 20) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), UserError.PASS_INVALID.getMessage()), HttpStatus.BAD_REQUEST);
        }
        User user = userService.createNewUser(registrationUserDto);
        return ResponseEntity.ok(new UserDto(user.getId(), user.getUsername()));
    }

    /**
     * Обновляет токен аутентификации.
     * @param oldToken Токен аутентификации
     * @return ResponseEntity с обновленным токеном
     */
    public ResponseEntity<?> updateAuthToken(String oldToken) {
        oldToken = oldToken.substring(7);
        UserDetails userDetails = userService.loadUserByUsername(jwtTokenUtils.getUsername(oldToken));
        String token = jwtTokenUtils.generateToken(userDetails);
        return ResponseEntity.ok(
            new UserWithJwtResponse(
                jwtTokenUtils.getUsername(token), 
                userService.loadRolesByUsername(jwtTokenUtils.getUsername(token)).stream().map(Object::toString).collect(Collectors.toList()), 
                token));
    }
}
