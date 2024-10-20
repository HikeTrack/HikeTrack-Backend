package com.hiketrackbackend.hiketrackbackend.model.tour.details;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Activity {
    HIKING("Hiking"),
    BIKING("Biking"),
    CLIMBING("Climbing");

    private final String displayName;

    Activity(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

    @JsonCreator
    public static Activity forValue(String value) {
        if (value == null) {
            return null;
        }
        for (Activity activity : Activity.values()) {
            if (activity.displayName.equalsIgnoreCase(value)) {
                return activity;
            }
            if (activity.name().equalsIgnoreCase(value)) {
                return activity;
            }
        }
        throw new IllegalArgumentException("Unknown activity type: " + value);
    }
}
