package com.hiketrackbackend.hiketrackbackend.model.tour.details;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RouteType {
    POINT_TO_POINT("Point to Point"),
    ROUND_TRIP("Round Trip"),
    MULTI_DESTINATION("Multi Destination"),
    OPEN_JAW("Open Jaw");

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
