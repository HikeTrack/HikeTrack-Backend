package com.hiketrackbackend.hiketrackbackend.repository.tour;

import com.hiketrackbackend.hiketrackbackend.dto.tour.TourSearchParameters;
import com.hiketrackbackend.hiketrackbackend.model.tour.Tour;
import com.hiketrackbackend.hiketrackbackend.repository.SpecificationBuilder;
import com.hiketrackbackend.hiketrackbackend.repository.SpecificationProviderManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TourSpecificationBuilder implements SpecificationBuilder<Tour, TourSearchParameters> {
    private static final String ROUTE_TYPE_KEYWORD = "routeType";
    private static final String DIFFICULTY_KEYWORD = "difficulty";
    private static final String LENGTH_KEYWORD = "length";
    private static final String ACTIVITY_KEYWORD = "activity";
    private static final String DATE_KEYWORD = "date";
    private static final String DURATION_KEYWORD = "duration";
    private static final String PRICE_KEYWORD = "price";
    private static final String COUNTRY_KEYWORD = "country";
    private final SpecificationProviderManager<Tour> tourSpecificationProviderManager;

    @Override
    public Specification<Tour> build(TourSearchParameters searchParameters) {
        Specification<Tour> spec = Specification.where(null);
        if (searchParameters.routeType() != null && searchParameters.routeType().length > 0) {
            spec = spec.and(tourSpecificationProviderManager
                    .getSpecificationProvider(ROUTE_TYPE_KEYWORD)
                    .getSpecification(searchParameters.routeType()));
        }
        if (searchParameters.difficulty() != null && searchParameters.difficulty().length > 0) {
            spec = spec.and(tourSpecificationProviderManager
                    .getSpecificationProvider(DIFFICULTY_KEYWORD)
                    .getSpecification(searchParameters.difficulty()));
        }
        if ((searchParameters.minLength() != null
                && searchParameters.minLength().length > 0)
                && (searchParameters.maxLength() != null
                && searchParameters.maxLength().length > 0)
        ) {
            spec = spec.and(tourSpecificationProviderManager
                    .getSpecificationProvider(LENGTH_KEYWORD)
                    .getSpecification(searchParameters.minLength(), searchParameters.maxLength()));
        }
        if (searchParameters.activity() != null && searchParameters.activity().length > 0) {
            spec = spec.and(tourSpecificationProviderManager
                    .getSpecificationProvider(ACTIVITY_KEYWORD)
                    .getSpecification(searchParameters.activity()));
        }
        if ((searchParameters.startDate() != null && searchParameters.startDate().length > 0)
                && (searchParameters.endDate() != null && searchParameters.endDate().length > 0)) {
            spec = spec.and(tourSpecificationProviderManager
                    .getSpecificationProvider(DATE_KEYWORD)
                    .getSpecification(searchParameters.startDate(), searchParameters.endDate()));
        }
        if ((searchParameters.minDuration() != null
                && searchParameters.minDuration().length > 0)
                && (searchParameters.maxDuration() != null
                && searchParameters.maxDuration().length > 0)
        ) {
            spec = spec.and(tourSpecificationProviderManager
                    .getSpecificationProvider(DURATION_KEYWORD)
                    .getSpecification(
                            searchParameters.minDuration(),
                            searchParameters.maxDuration())
            );
        }
        if ((searchParameters.minPrice() != null
                && searchParameters.minPrice().length > 0)
                && (searchParameters.maxPrice() != null
                && searchParameters.maxPrice().length > 0)) {
            spec = spec.and(tourSpecificationProviderManager
                    .getSpecificationProvider(PRICE_KEYWORD)
                    .getSpecification(searchParameters.minPrice(), searchParameters.maxPrice()));
        }
        if (searchParameters.country() != null && searchParameters.country().length > 0) {
            spec = spec.and(tourSpecificationProviderManager
                    .getSpecificationProvider(COUNTRY_KEYWORD)
                    .getSpecification(searchParameters.country()));
        }
        return spec;
    }
}
