package com.hiketrackbackend.hiketrackbackend.repository.tour;

import com.hiketrackbackend.hiketrackbackend.model.tour.Tour;
import com.hiketrackbackend.hiketrackbackend.model.tour.details.TourDetails;
import com.hiketrackbackend.hiketrackbackend.repository.SpecificationProvider;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class DurationSpecificationProvider implements SpecificationProvider<Tour> {
    private static final String NAME_PARAMETER = "duration";

    @Override
    public String getKey() {
        return NAME_PARAMETER;
    }

    @Override
    public Specification<Tour> getSpecification(String[] minParam, String[] maxParam) {
        try {
            int minDuration = Integer.parseInt(minParam[0]);
            int maxDuration = Integer.parseInt(maxParam[0]);

            return (root, query, criteriaBuilder) -> {
                Join<Tour, TourDetails> detailsJoin = root.join("tourDetails", JoinType.LEFT);
                return criteriaBuilder.between(
                        detailsJoin.get(NAME_PARAMETER), minDuration, maxDuration
                );
            };
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Number should be used for search param in duration specification"
            );
        }
    }
}
