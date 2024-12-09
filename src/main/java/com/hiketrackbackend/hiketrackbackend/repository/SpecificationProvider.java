package com.hiketrackbackend.hiketrackbackend.repository;

import java.util.Arrays;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationProvider<T> {
    String getKey();

    default Specification<T> getSpecification(String[] params) {
        return (root, query, criteriaBuilder) -> root.get(getKey())
                .in(Arrays.stream(params).toArray());
    }

    default Specification<T> getSpecification(String[] minParam, String[] maxParam) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get(getKey()), minParam[0], maxParam[0]);
    }
}
