package com.hiketrackbackend.hiketrackbackend.dto.tour;

public record TourSearchParameters(
        String[] routeType,
        String[] difficulty,
        String[] minLength,
        String[] maxLength,
        String[] activity,
        String[] startDate,
        String[] endDate,
        String[] minDuration,
        String[] maxDuration,
        String[] minPrice,
        String[] maxPrice,
        String[] country
) {
}
