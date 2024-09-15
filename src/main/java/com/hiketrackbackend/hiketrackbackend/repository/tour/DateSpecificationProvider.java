package com.hiketrackbackend.hiketrackbackend.repository.tour;

import com.hiketrackbackend.hiketrackbackend.model.tour.Tour;
import com.hiketrackbackend.hiketrackbackend.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DateSpecificationProvider implements SpecificationProvider<Tour> {
    private static final String NAME_PARAMETER = "date";

    @Override
    public String getKey() {
        return NAME_PARAMETER;
    }

    //find better logic sorting
    @Override
    public Specification<Tour> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            List<LocalDateTime> dates = Arrays.stream(params)
                    .map(param -> LocalDateTime.parse(param, formatter))
                    .collect(Collectors.toList());
            return root.get(NAME_PARAMETER).in(dates);
        };
    }
}
