package com.hiketrackbackend.hiketrackbackend.repository.tour;

import com.hiketrackbackend.hiketrackbackend.model.tour.Tour;
import com.hiketrackbackend.hiketrackbackend.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class PriceSpecificationProvider implements SpecificationProvider<Tour> {
    private static final String NAME_PARAMETER = "price";

    @Override
    public String getKey() {
        return NAME_PARAMETER;
    }

    @Override
    public Specification<Tour> getSpecification(String[] minParam, String[] maxParam) {
        try {
            double minPrice = Double.parseDouble(minParam[0]);
            double maxPrice = Double.parseDouble(maxParam[0]);

            return (root, query, criteriaBuilder) ->
                    criteriaBuilder.between(root.get(NAME_PARAMETER), minPrice, maxPrice);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Number should be used for search param in price specification"
            );
        }
    }
}
