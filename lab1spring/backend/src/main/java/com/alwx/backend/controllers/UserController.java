package com.alwx.backend.controllers;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alwx.backend.dtos.AdminRightsRequest;
import com.alwx.backend.dtos.AppError;
import com.alwx.backend.dtos.NewVehicle;
import com.alwx.backend.dtos.SimpleInfoAboutCars;
import com.alwx.backend.models.Vehicle;
import com.alwx.backend.service.AuthService;
import com.alwx.backend.service.UserService;
import com.alwx.backend.service.VehicleService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;



@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping("/user")
public class UserController {

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/vehicles")
    public List<SimpleInfoAboutCars> getTableWithVehicle(){
        
        List<? extends Vehicle> carsForUsers = vehicleService.getAllVehicle();
        List<SimpleInfoAboutCars> request = new ArrayList<>();
        for(Vehicle vehicle : carsForUsers){
            request.add(new SimpleInfoAboutCars(vehicle));
        }
        
        return request;
    }

    @PostMapping("/vehicles")
    public ResponseEntity<?> createVehicle(@Valid @RequestBody NewVehicle newVehicle, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
            return ResponseEntity
                .badRequest()
                .body(new AppError(HttpStatus.BAD_REQUEST.value(), errors.toString()));
        }

        ResponseEntity<?> response = vehicleService.createVehicle(newVehicle);
        
        messagingTemplate.convertAndSend("/topic/tableUpdates", 
            "{\"message\": \"Данные в таблице обновлены\"}");
        
        return response;
    }

    @PostMapping("/rights")
    public ResponseEntity<?> getAdminRights(@RequestBody AdminRightsRequest adminRightsRequest){
        
        ResponseEntity<?> response = userService.pushReqForAdminRights(adminRightsRequest);
        
        return response;
    }

    @GetMapping("/token")
    public ResponseEntity<?> updateToken(@RequestHeader(name = "Authorization") String token){
        return authService.updateAuthToken(token);
    }
}

