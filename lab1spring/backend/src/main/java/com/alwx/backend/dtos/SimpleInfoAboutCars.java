package com.alwx.backend.dtos;

import java.util.*;
import java.util.stream.Collectors;

import com.alwx.backend.models.User;
import com.alwx.backend.models.Vehicle;

import lombok.Data;

@Data
public class SimpleInfoAboutCars {
    private Long id; 
    private String name;
    private Long x;
    private Double y;
    private String type;
    private Double enginePower;
    private long numberOfWheels;
    private Long capacity;
    private Double distanceTravelled;
    private Float fuelConsumption;
    private String fuelType;
    private List<String> namesUsers;
    private Boolean permissionToEdit;

    /**
     * Конструктор для создания объекта SimpleInfoAboutCars.
     * @param vehicle Объект Vehicle, который будет преобразован в SimpleInfoAboutCars
     */
    public SimpleInfoAboutCars(Vehicle vehicle){
        this.id = vehicle.getId();
        this.name = vehicle.getName();
        this.x = vehicle.getCoordinates().getX();
        this.y = vehicle.getCoordinates().getY();
        this.type = vehicle.getType().name();
        this.enginePower = vehicle.getEnginePower();
        this.numberOfWheels = vehicle.getNumberOfWheels();
        this.capacity = vehicle.getCapacity();
        this.distanceTravelled = vehicle.getDistanceTravelled();
        this.fuelConsumption = vehicle.getFuelConsumption();
        this.fuelType = vehicle.getFuelType().name();
        this.namesUsers = vehicle.getUsers().stream().map(User::getUsername).collect(Collectors.toList());
        this.permissionToEdit = vehicle.getPermissionToEdit();
    }
}
