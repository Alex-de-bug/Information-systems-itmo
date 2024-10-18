package com.alwx.backend.dtos;


import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NewVehicle {
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
    private List<String> namesOfOwners;
    private Boolean permissionToEdit;

    
}
