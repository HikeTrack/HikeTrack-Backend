package com.hiketrackbackend.hiketrackbackend.repository.tour;

import com.hiketrackbackend.hiketrackbackend.exception.SpecificationNotFoundException;
import com.hiketrackbackend.hiketrackbackend.model.tour.Tour;
import com.hiketrackbackend.hiketrackbackend.repository.SpecificationProvider;
import com.hiketrackbackend.hiketrackbackend.repository.SpecificationProviderManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;

@RequiredArgsConstructor
@Component
public class TourSpecificationProviderManager implements SpecificationProviderManager<Tour> {
    private final List<SpecificationProvider<Tour>> tourSpecificationProviders;

    @Override
    public SpecificationProvider<Tour> getSpecificationProvider(String key) {
        return tourSpecificationProviders.stream()
                .filter(b -> b.getKey().equals(key))
                .findFirst()
                .orElseThrow(() -> new SpecificationNotFoundException
                        ("Can`t find correct specification provider for key: " + key));
    }
}
