package com.hiketrackbackend.hiketrackbackend.repository;

import com.hiketrackbackend.hiketrackbackend.model.country.Continent;
import com.hiketrackbackend.hiketrackbackend.model.country.Country;
import com.hiketrackbackend.hiketrackbackend.model.tour.Difficulty;
import com.hiketrackbackend.hiketrackbackend.model.tour.Review;
import com.hiketrackbackend.hiketrackbackend.model.tour.Tour;
import com.hiketrackbackend.hiketrackbackend.model.user.User;
import com.hiketrackbackend.hiketrackbackend.repository.country.CountryRepository;
import com.hiketrackbackend.hiketrackbackend.repository.tour.TourRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private TourRepository tourRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CountryRepository countryRepository;
    private User user;
    private Tour tour;
    private Review review;
    private Country country;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
        reviewRepository.deleteAll();
        tourRepository.deleteAll();
        countryRepository.deleteAll();

        user = new User();
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("password");
        user = userRepository.save(user);

        country = new Country();
        country.setContinent(Continent.EUROPE);
        country.setPhoto("photo.jpg");
        country.setName("Test");
        country = countryRepository.save(country);

        tour = new Tour();
        tour.setName("Test Tour");
        tour.setLength(10);
        tour.setPrice(BigDecimal.valueOf(100.00));
        tour.setDate(ZonedDateTime.now());
        tour.setDifficulty(Difficulty.Easy);
        tour.setMainPhoto("photo.jpg");
        tour.setUser(user);
        tour.setCountry(country);
        tour.setDeleted(false);
        tour = tourRepository.save(tour);

        review = new Review();
        review.setContent("Great tour!");
        review.setDateCreated(LocalDateTime.now());
        review.setUser(user);
        review.setTour(tour);
        review = reviewRepository.save(review);
    }

    @Test
    @DisplayName("Find review by tour id")
    public void testFindByTourIdWhenReviewExistsThenReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Review> result = reviewRepository.findByTourId(tour.getId(), pageable);

        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).contains(review);
    }

    @Test
    @DisplayName("Find review by tour id when its not exist")
    public void testFindByTourIdWhenReviewDoesNotExistThenReturnEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Review> result = reviewRepository.findByTourId(-1L, pageable);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Is review exist by id and tour id")
    public void testExistsByIdAndTourIdWhenReviewExistsThenReturnTrue() {
        boolean exists = reviewRepository.existsByIdAndTourId(review.getId(), tour.getId());

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Is review exist by id and tour id negative result")
    public void testExistsByIdAndTourIdWhenReviewDoesNotExistThenReturnFalse() {
        boolean exists = reviewRepository.existsByIdAndTourId(-1L, tour.getId());

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Find review by user id")
    public void testFindReviewsByUserIdWhenReviewExistsThenReturnList() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Review> result = reviewRepository.findReviewsByUserId(user.getId(), pageable);

        assertThat(result).isNotEmpty();
        assertThat(result).contains(review);
    }

    @Test
    @DisplayName("Find review by user id when its not exist")
    public void testFindReviewsByUserIdWhenReviewDoesNotExistThenReturnEmptyList() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Review> result = reviewRepository.findReviewsByUserId(-1L, pageable);

        assertThat(result).isEmpty();
    }
}
