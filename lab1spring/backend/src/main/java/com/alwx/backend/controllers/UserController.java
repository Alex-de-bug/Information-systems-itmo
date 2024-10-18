package com.alwx.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.alwx.backend.dtos.NewVehicle;
import com.alwx.backend.service.VehicleService;

import jakarta.transaction.Transactional;

@RestController
@CrossOrigin("*")
public class UserController {

    @Autowired
    private VehicleService vehicleService;

    @PostMapping("/vehicles")
    @Transactional
    public ResponseEntity<?> createVehicle(@RequestBody NewVehicle newVehicle){

        return vehicleService.createVehicle(newVehicle);
    }
}

