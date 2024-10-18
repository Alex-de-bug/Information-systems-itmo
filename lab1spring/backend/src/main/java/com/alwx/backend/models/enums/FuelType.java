package com.alwx.backend.models.enums;

public enum FuelType {
    KEROSENE,
    ELECTRICITY,
    DIESEL,
    MANPOWER,
    PLASMA;

    public static FuelType fromString(String value) {
        try {
            return FuelType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("No enum constant for value: " + value);
        }
    }
}
