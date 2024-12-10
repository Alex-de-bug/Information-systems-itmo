package com.alwx.backend.models.enums;

/**
 * Перечисление типов топлива, которые могут быть использованы в автомобилях.
 */
public enum FuelType {
    KEROSENE("КЕРОСИН"),
    ELECTRICITY("ЭЛЕКТРИЧЕСТВО"),
    DIESEL("ДИЗЕЛЬ"),
    MANPOWER("ЧЕЛОВЕЧЕСКИЕ УСИЛИЯ"),
    PLASMA("ПЛАЗМА");

    private final String russianName;

    FuelType(String russianName) {
        this.russianName = russianName;
    }

    /**
     * Преобразует строку в соответствующий тип топлива.
     * @param value Строка, представляющая тип топлива (на английском или русском)
     * @return Тип топлива
     * @throws IllegalArgumentException Если строка не представляет допустимый тип топлива
     */
    public static FuelType fromString(String value) {
        String upperValue = value.toUpperCase();
        for (FuelType type : FuelType.values()) {
            if (type.name().equals(upperValue) || type.russianName.equals(upperValue)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Нет типа топлива: " + value);
    }

    public String getRussianName() {
        return russianName;
    }
}