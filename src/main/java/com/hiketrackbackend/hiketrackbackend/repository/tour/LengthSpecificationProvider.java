package com.hiketrackbackend.hiketrackbackend.repository.tour;

import com.hiketrackbackend.hiketrackbackend.model.tour.Tour;
import com.hiketrackbackend.hiketrackbackend.repository.SpecificationProvider;
import java.util.Arrays;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class LengthSpecificationProvider implements SpecificationProvider<Tour> {
    private static final String NAME_PARAMETER = "length";

    @Override
    public String getKey() {
        return NAME_PARAMETER;
    }

    @Override
    public Specification<Tour> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) -> root.get(NAME_PARAMETER)
                .in(Arrays.stream(params).toArray());
    }
}
