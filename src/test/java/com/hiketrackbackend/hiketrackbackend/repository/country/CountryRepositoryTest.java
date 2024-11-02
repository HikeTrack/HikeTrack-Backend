package com.hiketrackbackend.hiketrackbackend.repository.country;

import com.hiketrackbackend.hiketrackbackend.model.country.Continent;
import com.hiketrackbackend.hiketrackbackend.model.country.Country;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CountryRepositoryTest {
    @Autowired
    private CountryRepository countryRepository;

    @BeforeEach
    public void setUp() {
        countryRepository.deleteAll();

        for (int i = 1; i <= 15; i++) {
            Country country = new Country();
            country.setName("Country" + i);
            country.setPhoto("Photo" + i);
            country.setContinent(Continent.EUROPE);
            countryRepository.save(country);
        }
    }

    @Test
    @DisplayName("Successfully can find 10 random countries")
    public void testFindTenRandomCountryWhenMoreThanTenCountriesThenReturnTenCountries() {
        List<Country> randomCountries = countryRepository.findTenRandomCountry();

        assertThat(randomCountries).hasSize(10);
    }

    @Test
    @DisplayName("Successfully find country by its name")
    public void testFindCountryByNameWhenCountryExistsThenReturnCountry() {
        String countryName = "TestCountry";
        Country country = new Country();
        country.setName(countryName);
        country.setPhoto("TestPhoto");
        country.setContinent(Continent.ASIA);
        countryRepository.save(country);

        Optional<Country> foundCountry = countryRepository.findCountryByName(countryName);

        assertThat(foundCountry).isPresent();
        assertThat(foundCountry.get().getName()).isEqualTo(countryName);
    }

    @Test
    @DisplayName("Find country by not valid name")
    public void testFindCountryByNameWhenCountryDoesNotExistThenReturnEmptyOptional() {
        Optional<Country> foundCountry = countryRepository.findCountryByName("NonExistentCountry");

        assertThat(foundCountry).isNotPresent();
    }
}
