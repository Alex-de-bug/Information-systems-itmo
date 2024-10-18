package com.alwx.backend.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.alwx.backend.dtos.NewVehicle;
import com.alwx.backend.models.Coordinates;
import com.alwx.backend.models.User;
import com.alwx.backend.models.Vehicle;
import com.alwx.backend.models.enums.FuelType;
import com.alwx.backend.models.enums.VehicleType;
import com.alwx.backend.repositories.CoordinatesRepositury;
import com.alwx.backend.repositories.UserRepository;
import com.alwx.backend.repositories.VehicleRepository;

import jakarta.transaction.Transactional;

@Service
public class VehicleService {
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private CoordinatesRepositury coordinatesRepositury;
    @Autowired
    private UserRepository userRepository;

    public List<? extends Vehicle> getAllVehicle(){
        return vehicleRepository.findAll();
    }

    @Transactional
    public ResponseEntity<?> createVehicle(NewVehicle newVehicle){
        Vehicle vehicle = new Vehicle();
        vehicle.setName(newVehicle.getName());

        if(coordinatesRepositury.findByXAndY(newVehicle.getX(), newVehicle.getY()).isPresent()){
            vehicle.setCoordinates(coordinatesRepositury.findByXAndY(newVehicle.getX(), newVehicle.getY()).get());
        }else{
            Coordinates coordinates = new Coordinates();
            coordinates.setX(newVehicle.getX());
            coordinates.setY(newVehicle.getY());
            vehicle.setCoordinates(coordinates);
        }

        LocalDateTime localDateTime = LocalDateTime.now();
        vehicle.setCreationDate(localDateTime);

        VehicleType vehicleType = VehicleType.fromString(newVehicle.getType());
        vehicle.setType(vehicleType);

        vehicle.setEnginePower(newVehicle.getEnginePower());

        vehicle.setNumberOfWheels(newVehicle.getNumberOfWheels());

        vehicle.setCapacity(newVehicle.getCapacity());

        vehicle.setDistanceTravelled(newVehicle.getDistanceTravelled());

        vehicle.setFuelConsumption(newVehicle.getFuelConsumption());

        FuelType fuelType = FuelType.fromString(newVehicle.getFuelType());

        vehicle.setFuelType(fuelType);

        List<String> owners = newVehicle.getNamesOfOwners();
        List<User> convertOwners = new ArrayList<>();
        for(String owner : owners){
            if(userRepository.findByUsername(owner).isPresent()){
                convertOwners.add(userRepository.findByUsername(owner).get());
            }
        }

        vehicle.setUsers(convertOwners);

        vehicle.setPermissionToEdit(newVehicle.getPermissionToEdit());

        vehicleRepository.save(vehicle);

        Hibernate.initialize(vehicle.getUsers());

        return ResponseEntity.ok("Вы успешно добавили машину");
    }
}
