package com.hiketrackbackend.hiketrackbackend.repository.tour;

import com.hiketrackbackend.hiketrackbackend.model.tour.Tour;
import com.hiketrackbackend.hiketrackbackend.repository.SpecificationProvider;
import org.springframework.stereotype.Component;

@Component
public class DifficultySpecificationProvider implements SpecificationProvider<Tour> {
    private static final String NAME_PARAMETER = "difficulty";

    @Override
    public String getKey() {
        return NAME_PARAMETER;
    }
}
