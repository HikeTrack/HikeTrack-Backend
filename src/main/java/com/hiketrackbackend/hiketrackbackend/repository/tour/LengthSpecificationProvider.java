package com.hiketrackbackend.hiketrackbackend.repository.tour;

import com.hiketrackbackend.hiketrackbackend.model.tour.Tour;
import com.hiketrackbackend.hiketrackbackend.repository.SpecificationProvider;
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
    public Specification<Tour> getSpecification(String[] minParam, String[] maxParam) {
        try {
            int minLength = Integer.parseInt(minParam[0]);
            int maxLength = Integer.parseInt(maxParam[0]);

            return (root, query, criteriaBuilder) ->
                    criteriaBuilder.between(root.get(NAME_PARAMETER), minLength, maxLength);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Number should be used for search param in length specification"
            );
        }
    }
}
