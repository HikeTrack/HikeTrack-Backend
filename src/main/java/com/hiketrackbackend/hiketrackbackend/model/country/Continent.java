package com.hiketrackbackend.hiketrackbackend.model.country;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Continent {
    EUROPE("Europe"),
    ASIA("Asia"),
    SOUTH_AMERICA("South America"),
    NORTH_AMERICA("North America"),
    AFRICA("Africa"),
    AUSTRALIA("Australia");

    private final String displayName;

    Continent(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

    @JsonCreator
    public static Continent forValue(String value) {
        if (value == null) {
            return null;
        }
        for (Continent continent : Continent.values()) {
            if (continent.displayName.equalsIgnoreCase(value)) {
                return continent;
            }
            if (continent.name().equalsIgnoreCase(value)) {
                return continent;
            }
        }
        throw new IllegalArgumentException("Unknown continent type: " + value);
    }
}
