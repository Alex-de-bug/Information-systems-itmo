package com.alwx.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.alwx.backend.dtos.NewVehicle;
import com.alwx.backend.service.VehicleService;


@RestController
@CrossOrigin("*")
public class UserController {

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate; 

    @PostMapping("/vehicles")
    @SendTo("/topic/tableUpdates")
    public ResponseEntity<?> createVehicle(@RequestBody NewVehicle newVehicle){

        ResponseEntity<?> response = vehicleService.createVehicle(newVehicle);

        messagingTemplate.convertAndSend("/topic/tableUpdates", "Машины обновлены");
        
        return response;
    }
}

