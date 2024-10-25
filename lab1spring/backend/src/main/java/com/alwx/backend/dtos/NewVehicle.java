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
    @Pattern(regexp = "^(PLANE|BOAT|BICYCLE)$", message = "Неверный тип транспорта")
    private String type;

    @NotNull(message = "Мощность двигателя обязательна")
    @Positive(message = "Мощность двигателя должна быть положительной")
    private Double enginePower;

    @Min(value = 1, message = "Количество колес должно быть не менее 1")
    private long numberOfWheels;

    @NotNull(message = "Вместимость обязательна")
    @Positive(message = "Вместимость должна быть положительной")
    private Long capacity;

    @PositiveOrZero(message = "Пройденное расстояние не может быть отрицательным")
    private Double distanceTravelled;

    @Positive(message = "Расход топлива должен быть положительным")
    private Float fuelConsumption;

    @NotBlank(message = "Тип топлива не может быть пустым")
    @Pattern(regexp = "^(KEROSENE|DIESEL|ELECTRICITY|MANPOWER|PLASMA)$", message = "Неверный тип топлива")
    private String fuelType;

    @NotEmpty(message = "Имена владельцев не могут быть пустыми")
    private List<String> namesOfOwners;

    private Boolean permissionToEdit;
}