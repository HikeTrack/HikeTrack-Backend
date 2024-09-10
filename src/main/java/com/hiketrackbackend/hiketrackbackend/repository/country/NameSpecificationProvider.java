package com.hiketrackbackend.hiketrackbackend.repository.country;

import com.hiketrackbackend.hiketrackbackend.model.Country;
import com.hiketrackbackend.hiketrackbackend.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Component
public class NameSpecificationProvider implements SpecificationProvider<Country> {
    private static final String NAME_PARAMETER = "name";

    @Override
    public String getKey() {
        return NAME_PARAMETER;
    }

    @Override
    public Specification<Country> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) -> root.get(NAME_PARAMETER)
                .in(Arrays.stream(params).toArray());
    }
}
