package com.hiketrackbackend.hiketrackbackend.repository.tour;

import com.hiketrackbackend.hiketrackbackend.dto.tour.TourSearchParameters;
import com.hiketrackbackend.hiketrackbackend.model.Tour;
import com.hiketrackbackend.hiketrackbackend.repository.SpecificationBuilder;
import com.hiketrackbackend.hiketrackbackend.repository.SpecificationProviderManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TourSpecificationBuilder implements SpecificationBuilder<Tour, TourSearchParameters> {
    private static final String LIKES_KEYWORD = "likes";
    private final SpecificationProviderManager<Tour> tourSpecificationProviderManager;

    @Override
    public Specification<Tour> build(TourSearchParameters searchParameters) {
        Specification<Tour> spec = Specification.where(null);
        if (searchParameters.tourLikes() != null && searchParameters.tourLikes().length > 0) {
            spec = spec.and(tourSpecificationProviderManager
                    .getSpecificationProvider(LIKES_KEYWORD)
                    .getSpecification(searchParameters.tourLikes()));
        }
        return spec;
    }


}
