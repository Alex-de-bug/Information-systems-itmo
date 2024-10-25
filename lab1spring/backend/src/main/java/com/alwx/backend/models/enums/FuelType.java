package com.alwx.backend.models.enums;

/**
 * Перечисление типов топлива, которые могут быть использованы в автомобилях.
 */
public enum FuelType {
    KEROSENE,
    ELECTRICITY,
    DIESEL,
    MANPOWER,
    PLASMA;

    /**
     * Преобразует строку в соответствующий тип топлива.
     * @param value Строка, представляющая тип топлива
     * @return Тип топлива
     * @throws IllegalArgumentException Если строка не представляет допустимый тип топлива
     */
    public static FuelType fromString(String value) {
        try {
            return FuelType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("No enum constant for value: " + value);
        }
    }
}
