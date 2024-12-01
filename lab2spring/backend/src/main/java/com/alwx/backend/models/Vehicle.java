package com.alwx.backend.models;

import java.time.LocalDateTime;
import java.util.Collection;


import com.alwx.backend.models.enums.FuelType;
import com.alwx.backend.models.enums.VehicleType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Модель для тс.
 */
@Entity
@Data
@Table(name = "vehicle")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 

    @NotNull
    @NotEmpty
    @Column(length = 255, unique = true)
    private String name;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "coordinates_id")
    private Coordinates coordinates;

    @NotNull
    private LocalDateTime creationDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 255)
    private VehicleType type;

    @NotNull
    @Min(1) 
    private Double enginePower;

    @Min(1)
    private long numberOfWheels;

    @NotNull
    @Min(1)
    private Long capacity;
    
    @NotNull
    @Min(1)
    private Double distanceTravelled;
    
    @NotNull
    @Min(1)
    private Float fuelConsumption;

    @Enumerated(EnumType.STRING)
    @Column(length = 255)
    private FuelType fuelType;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "vehicle_user",
        joinColumns = @JoinColumn(name = "vehicle_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Collection<User> users;

    private Boolean permissionToEdit;

}
