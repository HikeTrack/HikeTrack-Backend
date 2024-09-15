package com.hiketrackbackend.hiketrackbackend.dto.tour;

public record TourSearchParameters(
        String[] routeType,
        String[] difficulty,
        String[] length,
        String[] activity,
        String[] date,
        String[] duration,
        String[] price
) {
}
