package com.alwx.backend.models.enums;

/**
 * Перечисление типов транспортных средств.
 */
public enum VehicleType {
    PLANE,
    BOAT,
    BICYCLE;

    /**
     * Преобразует строку в соответствующий тип транспортного средства.
     * @param value Строка, представляющая тип транспортного средства
     * @return Тип транспортного средства
     * @throws IllegalArgumentException Если строка не представляет допустимый тип транспортного средства
     */
    public static VehicleType fromString(String value) {
        try {
            return VehicleType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("No enum constant for value: " + value);
        }
    }
}