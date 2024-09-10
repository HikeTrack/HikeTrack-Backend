package com.hiketrackbackend.hiketrackbackend.repository.country;

import com.hiketrackbackend.hiketrackbackend.exception.SpecificationNotFoundException;
import com.hiketrackbackend.hiketrackbackend.model.Country;
import com.hiketrackbackend.hiketrackbackend.repository.SpecificationProvider;
import com.hiketrackbackend.hiketrackbackend.repository.SpecificationProviderManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;

@RequiredArgsConstructor
@Component
public class CountrySpecificationProviderManager implements SpecificationProviderManager<Country> {
    private final List<SpecificationProvider<Country>> bookSpecificationProviders;

    @Override
    public SpecificationProvider<Country> getSpecificationProvider(String key) {
        return bookSpecificationProviders.stream()
                .filter(b -> b.getKey().equals(key))
                .findFirst()
                .orElseThrow(() -> new SpecificationNotFoundException
                        ("Can`t find correct specification provider for key: " + key));
    }
}
