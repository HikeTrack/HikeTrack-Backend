package com.hiketrackbackend.hiketrackbackend.model.tour.details;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RouteType {
    Point_to_Point("Point to Point"),
    Round_Trip("Round Trip"),
    Multi_Destination("Multi Destination"),
    Open_Jaw("Open Jaw");

    private final String displayName;

    RouteType(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }

    @JsonCreator
    public static RouteType forValue(String value) {
        if (value == null) {
            return null;
        }
        for (RouteType routeType : RouteType.values()) {
            if (routeType.displayName.equalsIgnoreCase(value)) {
                return routeType;
            }
            if (routeType.name().equalsIgnoreCase(value)) {
                return routeType;
            }
        }
        throw new IllegalArgumentException("Not exist rout type: " + value);
    }
}
