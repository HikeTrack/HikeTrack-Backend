package com.hiketrackbackend.hiketrackbackend.model.tour;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Difficulty {
    Easy("Easy"),
    Medium("Medium"),
    Hard("Hard");

    private final String displayName;

    Difficulty(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

    @JsonCreator
    public static Difficulty forValue(String value) {
        if (value == null) {
            return null;
        }
        for (Difficulty difficulty : Difficulty.values()) {
            if (difficulty.displayName.equalsIgnoreCase(value)) {
                return difficulty;
            }
            if (difficulty.name().equalsIgnoreCase(value)) {
                return difficulty;
            }
        }
        throw new IllegalArgumentException("Unknown difficulty type: " + value);
    }
}
