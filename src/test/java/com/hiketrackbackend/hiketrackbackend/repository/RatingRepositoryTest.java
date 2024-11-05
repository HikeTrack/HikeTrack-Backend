package com.hiketrackbackend.hiketrackbackend.repository;

import com.hiketrackbackend.hiketrackbackend.model.country.Continent;
import com.hiketrackbackend.hiketrackbackend.model.country.Country;
import com.hiketrackbackend.hiketrackbackend.model.tour.Difficulty;
import com.hiketrackbackend.hiketrackbackend.model.tour.Rating;
import com.hiketrackbackend.hiketrackbackend.model.tour.Tour;
import com.hiketrackbackend.hiketrackbackend.model.user.User;
import com.hiketrackbackend.hiketrackbackend.repository.country.CountryRepository;
import com.hiketrackbackend.hiketrackbackend.repository.tour.TourRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RatingRepositoryTest {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private TourRepository tourRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CountryRepository countryRepository;
    private User user;
    private Tour tour;
    private Country country;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
        ratingRepository.deleteAll();
        tourRepository.deleteAll();
        countryRepository.deleteAll();

        user = new User();
        user.setEmail("testuser@example.com");
        user.setPassword("password");
        user.setFirstName("Test");
        user.setLastName("User");
        userRepository.save(user);

        country = new Country();
        country.setContinent(Continent.EUROPE);
        country.setPhoto("photo.jpg");
        country.setName("Test");
        countryRepository.save(country);

        tour = new Tour();
        tour.setName("Test Tour");
        tour.setLength(10);
        tour.setPrice(BigDecimal.valueOf(100.00));
        tour.setDate(ZonedDateTime.now());
        tour.setDifficulty(Difficulty.EASY);
        tour.setMainPhoto("photo.jpg");
        tour.setCountry(country);
        tour.setUser(user);
        tour.setDeleted(false);
        tour = tourRepository.save(tour);
    }

    @Test
    @DisplayName("Find rating by user and tour id")
    public void testFindRatingByUserIdAndTourIdWhenRatingExists() {
        Rating rating = new Rating();
        rating.setRating(5);
        rating.setUser(user);
        rating.setTour(tour);

        ratingRepository.save(rating);

        Optional<Rating> foundRating = ratingRepository.findRatingByUser_IdAndTour_Id(user.getId(), tour.getId());

        assertThat(foundRating).isPresent();
        assertThat(foundRating.get().getRating()).isEqualTo(5L);
    }

    @Test
    @DisplayName("Find tour by user and tour id with not valid id")
    public void testFindRatingByUserIdAndTourIdWhenRatingDoesNotExist() {
        Long nonExistentUserId = 999L;
        Long nonExistentTourId = 999L;

        Optional<Rating> foundRating = ratingRepository.findRatingByUser_IdAndTour_Id(nonExistentUserId, nonExistentTourId);

        assertThat(foundRating).isNotPresent();
    }

    @Test
    @DisplayName("Find if rating exists by user and tour id")
    public void testExistsByUserIdAndTourIdWhenRatingExists() {
        Rating rating = new Rating();
        rating.setRating(5);
        rating.setUser(user);
        rating.setTour(tour);

        ratingRepository.save(rating);

        boolean exists = ratingRepository.existsByUserIdAndTourId(user.getId(), tour.getId());

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Is not exist rating by not valid ids")
    public void testExistsByUserIdAndTourIdWhenRatingDoesNotExist() {
        Long nonExistentUserId = 999L;
        Long nonExistentTourId = 999L;

        boolean exists = ratingRepository.existsByUserIdAndTourId(nonExistentUserId, nonExistentTourId);

        assertThat(exists).isFalse();
    }
}
