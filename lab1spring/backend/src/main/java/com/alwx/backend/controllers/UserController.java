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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alwx.backend.dtos.AdminRightsRequest;
import com.alwx.backend.dtos.AppError;
import com.alwx.backend.dtos.RequestVehicle;
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

    @PatchMapping("/vehicles/{id}")
    public ResponseEntity<?> updateVehicle(@RequestHeader(name = "Authorization") String token, @PathVariable("id") Long id, @Valid @RequestBody RequestVehicle newVehicle, BindingResult bindingResult){

        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(new AppError(HttpStatus.BAD_REQUEST.value(), errors.toString()));
        }

        ResponseEntity<?> response = vehicleService.updateVehicle(id, newVehicle, token.substring(7));
        if(response.getStatusCode().equals(HttpStatus.OK)){
            System.out.println("Данные в таблице обновлены");
            messagingTemplate.convertAndSend("/topic/tableUpdates", 
                "{\"message\": \"Данные в таблице обновлены\"}");
        }
        return response;
    }

    @DeleteMapping("/vehicles/{id}")
    public ResponseEntity<?> deleteVehicle(@PathVariable("id") Long id, @RequestHeader(name = "Authorization") String token, @RequestHeader(name = "Reassign-Vehicle-Id") String reassignId){
        ResponseEntity<?> response = vehicleService.deleteVehicle(id, token.substring(7), reassignId);
        if(response.getStatusCode().equals(HttpStatus.OK)){
            System.out.println("Данные в таблице обновлены");
            messagingTemplate.convertAndSend("/topic/tableUpdates", 
                "{\"message\": \"Данные в таблице обновлены\"}");
        }

        return response;
    }

    @PostMapping("/vehicles")
    public ResponseEntity<?> createVehicle(@Valid @RequestBody RequestVehicle newVehicle, BindingResult bindingResult) {
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
        if(response.getStatusCode().equals(HttpStatus.OK)){
            System.out.println("Данные в таблице обновлены");
            messagingTemplate.convertAndSend("/topic/tableUpdates", 
                "{\"message\": \"Данные в таблице обновлены\"}");
        }
        
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

