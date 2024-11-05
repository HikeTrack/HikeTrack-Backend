package com.hiketrackbackend.hiketrackbackend.repository.country;

import com.hiketrackbackend.hiketrackbackend.model.country.Country;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface CountryRepository
        extends JpaRepository<Country, Long>, JpaSpecificationExecutor<Country> {
    @Query(value = "SELECT * FROM countries ORDER BY RAND() LIMIT 10",
            nativeQuery = true)
    List<Country> findTenRandomCountry();

    Optional<Country> findCountryByName(String name);
}
