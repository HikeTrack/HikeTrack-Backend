package com.hiketrackbackend.hiketrackbackend.repository.tour;

import com.hiketrackbackend.hiketrackbackend.model.tour.Tour;
import com.hiketrackbackend.hiketrackbackend.repository.SpecificationProvider;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class DateSpecificationProvider implements SpecificationProvider<Tour> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String NAME_PARAMETER = "date";

    @Override
    public String getKey() {
        return NAME_PARAMETER;
    }

    @Override
    public Specification<Tour> getSpecification(String[] startParam, String[] endParam) {
        LocalDateTime startDate = LocalDateTime.parse(startParam[0], FORMATTER);
        LocalDateTime endDate = LocalDateTime.parse(endParam[0], FORMATTER);

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(
                        root.get(NAME_PARAMETER),
                        startDate,
                        endDate
                );
    }
}
