package com.hiketrackbackend.hiketrackbackend.repository.country;

import com.hiketrackbackend.hiketrackbackend.exception.SpecificationNotFoundException;
import com.hiketrackbackend.hiketrackbackend.model.country.Country;
import com.hiketrackbackend.hiketrackbackend.repository.SpecificationProvider;
import com.hiketrackbackend.hiketrackbackend.repository.SpecificationProviderManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CountrySpecificationProviderManager implements SpecificationProviderManager<Country> {
    private final List<SpecificationProvider<Country>> bookSpecificationProviders;

    @Override
    public SpecificationProvider<Country> getSpecificationProvider(String key) {
        return bookSpecificationProviders.stream()
                .filter(b -> b.getKey().equals(key))
                .findFirst().orElseThrow(
                        () -> new SpecificationNotFoundException(
                                "Can`t find correct specification provider for key: " + key));
    }
}
