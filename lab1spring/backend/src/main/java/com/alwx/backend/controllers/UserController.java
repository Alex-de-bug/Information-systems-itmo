package com.alwx.backend.controllers;

import java.util.*;

import org.hibernate.sql.model.ast.TableUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.alwx.backend.dtos.NewVehicle;
import com.alwx.backend.dtos.SimpleInfoAboutCars;
import com.alwx.backend.models.Vehicle;
import com.alwx.backend.service.VehicleService;
import com.alwx.backend.service.ws.TableUpdateService;



@RestController
@CrossOrigin("*")
public class UserController {

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private TableUpdateService tableUpdateService;

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
    @SendTo("/topic/tableUpdates")
    public ResponseEntity<?> createVehicle(@RequestBody NewVehicle newVehicle){

        ResponseEntity<?> response = vehicleService.createVehicle(newVehicle);
        tableUpdateService.notifyTableUpdate();
        
        return response;
    }
}

