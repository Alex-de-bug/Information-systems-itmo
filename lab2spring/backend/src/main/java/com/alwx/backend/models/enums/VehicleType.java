package com.alwx.backend.models.enums;

/**
 * Перечисление типов транспортных средств.
 */
public enum VehicleType {
    PLANE("САМОЛЁТ"),
    BOAT("ЛОДКА"),
    BICYCLE("ВЕЛОСИПЕД");

    private final String russianName;

    VehicleType(String russianName) {
        this.russianName = russianName;
    }

    /**
     * Преобразует строку в соответствующий тип транспортного средства.
     * @param value Строка, представляющая тип транспортного средства (на английском или русском)
     * @return Тип транспортного средства
     * @throws IllegalArgumentException Если строка не представляет допустимый тип транспортного средства
     */
    public static VehicleType fromString(String value) {
        String upperValue = value.toUpperCase();
        for (VehicleType type : VehicleType.values()) {
            if (type.name().equals(upperValue) || type.russianName.equals(upperValue)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Нет типа тс: " + value);
    }

    public String getRussianName() {
        return russianName;
    }
}