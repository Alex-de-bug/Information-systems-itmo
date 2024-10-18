package com.alwx.backend.models.enums;

public enum VehicleType {
    PLANE,
    BOAT,
    BICYCLE;

    public static VehicleType fromString(String value) {
        try {
            return VehicleType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("No enum constant for value: " + value);
        }
    }
}