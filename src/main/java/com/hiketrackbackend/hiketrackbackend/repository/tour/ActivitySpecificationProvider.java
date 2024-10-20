package com.hiketrackbackend.hiketrackbackend.repository.tour;

import com.hiketrackbackend.hiketrackbackend.model.tour.details.Details;
import com.hiketrackbackend.hiketrackbackend.model.tour.Tour;
import com.hiketrackbackend.hiketrackbackend.repository.SpecificationProvider;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Component
public class ActivitySpecificationProvider implements SpecificationProvider<Tour> {
    private static final String NAME_PARAMETER = "activity";

    @Override
    public String getKey() {
        return NAME_PARAMETER;
    }

    @Override
    public Specification<Tour> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) -> {
            Join<Tour, Details> detailsJoin = root.join("details", JoinType.LEFT);
            return detailsJoin.get(NAME_PARAMETER)
                    .in(Arrays.stream(params).toArray());
        };
    }
}
