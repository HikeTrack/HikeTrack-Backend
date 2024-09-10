package com.hiketrackbackend.hiketrackbackend.repository.country;

import com.hiketrackbackend.hiketrackbackend.dto.country.CountrySearchParameters;
import com.hiketrackbackend.hiketrackbackend.model.Country;
import com.hiketrackbackend.hiketrackbackend.repository.SpecificationBuilder;
import com.hiketrackbackend.hiketrackbackend.repository.SpecificationProviderManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CountrySpecificationBuilder implements SpecificationBuilder<Country> {
    private static final String CONTINENT_KEYWORD = "continent";
    private static final String NAME_KEYWORD = "name";
    private final SpecificationProviderManager<Country> countrySpecificationProviderManager;

    @Override
    public Specification<Country> build(CountrySearchParameters searchParameters) {
        Specification<Country> spec = Specification.where(null);
        if (searchParameters.continent() != null && searchParameters.continent().length > 0) {
            spec = spec.and(countrySpecificationProviderManager
                    .getSpecificationProvider(CONTINENT_KEYWORD)
                    .getSpecification(searchParameters.continent()));
        }
        if (searchParameters.country() != null && searchParameters.country().length > 0) {
            spec = spec.and(countrySpecificationProviderManager
                    .getSpecificationProvider(NAME_KEYWORD)
                    .getSpecification(searchParameters.country()));
        }
        return spec;
    }
}
