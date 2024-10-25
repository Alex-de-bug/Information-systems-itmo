package com.alwx.backend.dtos;

import jakarta.validation.constraints.*;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RequestVehicle {
    @NotBlank(message = "Имя машины не может быть пустым")
    @Size(max = 255, message = "Имя машины не может быть длиннее 255 символов")
    private String name;

    @NotNull(message = "Координата X обязательна")
    @Min(value = -308, message = "Координата X должна быть больше -308")
    private Long x;

    @NotNull(message = "Координата Y обязательна")
    @Max(value = 695, message = "Координата Y должна быть меньше 695")
    @Min(value = (long) Double.MIN_VALUE, message = "Координата Y должна быть больше 4.9*10^-324")
    private Double y;

    @NotBlank(message = "Тип транспорта обязателен")
    @Pattern(regexp = "^(PLANE|BOAT|BICYCLE)$", message = "Неверный тип транспорта")
    private String type;

    @NotNull(message = "Мощность двигателя обязательна")
    @Positive(message = "Мощность двигателя должна быть положительной")
    @Max(value = (long) Double.MAX_VALUE, message = "Мощность двигателя должна быть меньше 1.7*10^308")
    private Double enginePower;

    @Min(value = 1, message = "Количество колес должно быть не менее 1")
    private long numberOfWheels;

    @NotNull(message = "Вместимость обязательна")
    @Positive(message = "Вместимость должна быть положительной")
    private Long capacity;

    @PositiveOrZero(message = "Пройденное расстояние не может быть отрицательным")
    @Max(value = (long) Double.MAX_VALUE, message = "Пройденное расстояние должно быть меньше 1.7*10^308")
    private Double distanceTravelled;

    @Positive(message = "Расход топлива должен быть положительным")
    @Max(value = (long) Float.MAX_VALUE, message = "Расход топлива должен быть меньше 3.4*10^38")
    private Float fuelConsumption;

    @NotBlank(message = "Тип топлива не может быть пустым")
    @Pattern(regexp = "^(KEROSENE|DIESEL|ELECTRICITY|MANPOWER|PLASMA)$", message = "Неверный тип топлива")
    private String fuelType;

    @NotEmpty(message = "Имена владельцев не могут быть пустыми")
    private List<String> namesOfOwners;

    private Boolean permissionToEdit;
}