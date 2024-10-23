package com.alwx.backend.dtos;

import jakarta.validation.constraints.*;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NewVehicle {
    @NotBlank(message = "Имя машины не может быть пустым")
    @Size(max = 255, message = "Имя машины не может быть длиннее 255 символов")
    private String name;

    @NotNull(message = "Неверный тип координаты X")
    private Long x;

    @NotNull(message = "Неверный тип координаты Y")
    @Max(value = 695, message = "Y must be less than or equal to 695")
    private Double y;

    @NotBlank(message = "Неверный тип транспорта")
    @Pattern(regexp = "^(CAR|BOAT|BICYCLE|MOTORCYCLE)$", message = "Неверный тип транспорта")
    private String type;

    @NotNull(message = "Engine power is required")
    @Positive(message = "Engine power must be positive")
    private Double enginePower;

    @Min(value = 1, message = "Number of wheels must be at least 1")
    private long numberOfWheels;

    @NotNull(message = "Capacity is required")
    @Positive(message = "Capacity must be positive")
    private Long capacity;

    @PositiveOrZero(message = "Distance travelled cannot be negative")
    private Double distanceTravelled;

    @Positive(message = "Fuel consumption must be positive")
    private Float fuelConsumption;

    @NotBlank(message = "Fuel type cannot be empty")
    private String fuelType;

    @NotEmpty(message = "Names of owners cannot be empty")
    private List<String> namesOfOwners;

    private Boolean permissionToEdit;
}