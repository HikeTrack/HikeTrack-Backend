package com.hiketrackbackend.hiketrackbackend.repository.tour;

import com.hiketrackbackend.hiketrackbackend.model.country.Continent;
import com.hiketrackbackend.hiketrackbackend.model.country.Country;
import com.hiketrackbackend.hiketrackbackend.model.tour.Difficulty;
import com.hiketrackbackend.hiketrackbackend.model.tour.Review;
import com.hiketrackbackend.hiketrackbackend.model.tour.Tour;
import com.hiketrackbackend.hiketrackbackend.model.user.User;
import com.hiketrackbackend.hiketrackbackend.model.tour.Rating;
import com.hiketrackbackend.hiketrackbackend.repository.UserRepository;
import com.hiketrackbackend.hiketrackbackend.repository.country.CountryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TourRepositoryTest {

    @Autowired
    private TourRepository tourRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CountryRepository countryRepository;
    private Country country;
    private User user;

    @BeforeEach
    public void setUp() {
        tourRepository.deleteAll();

        user = new User();
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setPassword("password");
        user = userRepository.save(user);

        country = new Country();
        country.setName("Country 1");
        country.setPhoto("photo1.jpg");
        country.setContinent(Continent.EUROPE);
        country = countryRepository.save(country);
    }

    @Test
    @DisplayName("Find tour with the highest rating successfully")
    public void testFindTopToursWithHighestRatingsWhenToursExistThenReturnTopTours() {
        Tour tour1 = new Tour();
        tour1.setName("Tour 1");
        tour1.setLength(10);
        tour1.setPrice(BigDecimal.valueOf(100));
        tour1.setDate(ZonedDateTime.now());
        tour1.setDifficulty(Difficulty.Easy);
        tour1.setMainPhoto("photo1.jpg");
        tour1.setCountry(country);
        tour1.setUser(user);
        tour1.setDeleted(false);

        Tour tour2 = new Tour();
        tour2.setName("Tour 2");
        tour2.setLength(20);
        tour2.setPrice(BigDecimal.valueOf(200));
        tour2.setDate(ZonedDateTime.now());
        tour2.setDifficulty(Difficulty.Medium);
        tour2.setMainPhoto("photo2.jpg");
        tour2.setCountry(country);
        tour2.setUser(user);
        tour2.setDeleted(false);

        Rating rating1 = new Rating();
        rating1.setRating(5L);
        rating1.setUser(user);
        rating1.setTour(tour1);

        Rating rating2 = new Rating();
        rating2.setRating(3L);
        rating2.setUser(user);
        rating2.setTour(tour2);

        tour1.getRatings().add(rating1);
        tour2.getRatings().add(rating2);

        tourRepository.save(tour1);
        tourRepository.save(tour2);

        Pageable pageable = PageRequest.of(0, 10);

        List<Tour> topTours = tourRepository.findTopToursWithHighestRatings(pageable);

        assertThat(topTours).hasSize(2);
        assertThat(topTours.get(0).getName()).isEqualTo("Tour 1");
        assertThat(topTours.get(1).getName()).isEqualTo("Tour 2");
    }

    @Test
    @DisplayName("Find not existed tour")
    public void testFindTourByIdWhenTourExistsThenReturnTour() {
        Tour tour = new Tour();
        tour.setName("Tour 1");
        tour.setLength(10);
        tour.setPrice(BigDecimal.valueOf(100));
        tour.setDate(ZonedDateTime.now());
        tour.setDifficulty(Difficulty.Easy);
        tour.setMainPhoto("photo1.jpg");
        tour.setCountry(country);
        tour.setUser(user);
        tour.setDeleted(false);

        Tour savedTour = tourRepository.save(tour);

        Optional<Tour> foundTour = tourRepository.findTourById(savedTour.getId());

        assertThat(foundTour).isPresent();
        assertThat(foundTour.get().getName()).isEqualTo("Tour 1");
    }

    @Test
    @DisplayName("Find existed tour by name and user id")
    public void testExistsTourByUserIdAndNameWhenTourExistsThenReturnTrue() {
        Tour tour = new Tour();
        tour.setName("Tour 1");
        tour.setLength(10);
        tour.setPrice(BigDecimal.valueOf(100));
        tour.setDate(ZonedDateTime.now());
        tour.setDifficulty(Difficulty.Easy);
        tour.setMainPhoto("photo1.jpg");
        tour.setCountry(country);
        tour.setUser(user);
        tour.setDeleted(false);

        tourRepository.save(tour);

        boolean exists = tourRepository.existsTourByUserIdAndName(user.getId(), "Tour 1");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Find tour by review id")
    public void testFindTourByReviewsIdWhenReviewExistsThenReturnTour() {
        Tour tour = new Tour();
        tour.setName("Tour 1");
        tour.setLength(10);
        tour.setPrice(BigDecimal.valueOf(100));
        tour.setDate(ZonedDateTime.now());
        tour.setDifficulty(Difficulty.Easy);
        tour.setMainPhoto("photo1.jpg");
        tour.setCountry(country);
        tour.setUser(user);
        tour.setDeleted(false);

        Review review = new Review();
        review.setContent("Great tour!");
        review.setUser(user);
        review.setTour(tour);

        tour.getReviews().add(review);

        tourRepository.save(tour);

        Optional<Tour> foundTour = tourRepository.findTourByReviewsId(review.getId());

        assertThat(foundTour).isPresent();
        assertThat(foundTour.get().getName()).isEqualTo("Tour 1");
    }

    @Test
    @DisplayName("Find tour by tour id and user id")
    public void testFindTourByIdAndUserIdWhenTourExistsThenReturnTour() {
        Tour tour = new Tour();
        tour.setName("Tour 1");
        tour.setLength(10);
        tour.setPrice(BigDecimal.valueOf(100));
        tour.setDate(ZonedDateTime.now());
        tour.setDifficulty(Difficulty.Easy);
        tour.setMainPhoto("photo1.jpg");
        tour.setCountry(country);
        tour.setUser(user);
        tour.setDeleted(false);

        Tour savedTour = tourRepository.save(tour);

        Optional<Tour> foundTour = tourRepository.findTourByIdAndUserId(savedTour.getId(), user.getId());

        assertThat(foundTour).isPresent();
        assertThat(foundTour.get().getName()).isEqualTo("Tour 1");
    }

    @Test
    @DisplayName("Make sure that tour exists by id and user id")
    public void testExistsByIdAndUserIdWhenTourExistsThenReturnTrue() {
        Tour tour = new Tour();
        tour.setName("Tour 1");
        tour.setLength(10);
        tour.setPrice(BigDecimal.valueOf(100));
        tour.setDate(ZonedDateTime.now());
        tour.setDifficulty(Difficulty.Easy);
        tour.setMainPhoto("photo1.jpg");
        tour.setCountry(country);
        tour.setUser(user);
        tour.setDeleted(false);

        Tour savedTour = tourRepository.save(tour);

        boolean exists = tourRepository.existsByIdAndUserId(savedTour.getId(), user.getId());

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Find all tours by guide id")
    public void testFindAllTourByUserIdWhenToursExistThenReturnAllTours() {
        Tour tour1 = new Tour();
        tour1.setName("Tour 1");
        tour1.setLength(10);
        tour1.setPrice(BigDecimal.valueOf(100));
        tour1.setDate(ZonedDateTime.now());
        tour1.setDifficulty(Difficulty.Easy);
        tour1.setMainPhoto("photo1.jpg");
        tour1.setCountry(country);
        tour1.setUser(user);
        tour1.setDeleted(false);

        Tour tour2 = new Tour();
        tour2.setName("Tour 2");
        tour2.setLength(20);
        tour2.setPrice(BigDecimal.valueOf(200));
        tour2.setDate(ZonedDateTime.now());
        tour2.setDifficulty(Difficulty.Medium);
        tour2.setMainPhoto("photo2.jpg");
        tour2.setCountry(country);
        tour2.setUser(user);
        tour2.setDeleted(false);

        tourRepository.save(tour1);
        tourRepository.save(tour2);

        Pageable pageable = PageRequest.of(0, 10);

        List<Tour> tours = tourRepository.findAllTourByUserId(user.getId(), pageable);

        assertThat(tours).hasSize(2);
        assertThat(tours.get(0).getName()).isEqualTo("Tour 1");
        assertThat(tours.get(1).getName()).isEqualTo("Tour 2");
    }
}
