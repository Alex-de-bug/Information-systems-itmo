package com.alwx.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.alwx.backend.dtos.JwtRequest;
import com.alwx.backend.dtos.RegUserDto;
import com.alwx.backend.service.AuthService;

@RestController
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/auth")
    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest jwtRequest){
        return authService.createAuthToken(jwtRequest);
    }

    @PostMapping("/reg")
    public ResponseEntity<?> createNewUser(@RequestBody RegUserDto regUserDto){
        return authService.createNewUser(regUserDto);
    }
}
