package com.hiketrackbackend.hiketrackbackend.repository.tour;

import com.hiketrackbackend.hiketrackbackend.model.details.Details;
import com.hiketrackbackend.hiketrackbackend.model.tour.Tour;
import com.hiketrackbackend.hiketrackbackend.repository.SpecificationProvider;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class CountrySpecificationProvider implements SpecificationProvider<Tour> {
    private static final String NAME_PARAMETER = "country";

    @Override
    public String getKey() {
        return NAME_PARAMETER;
    }

    @Override
    public Specification<Tour> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) -> {
            Join<Tour, ?> countryJoin = root.join("country", JoinType.INNER);
            return countryJoin.get("name").in(params);
        };
    }
}
