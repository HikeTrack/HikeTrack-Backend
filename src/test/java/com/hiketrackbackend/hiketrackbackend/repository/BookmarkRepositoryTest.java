package com.hiketrackbackend.hiketrackbackend.repository;

import com.hiketrackbackend.hiketrackbackend.model.bookmark.Bookmark;
import com.hiketrackbackend.hiketrackbackend.model.bookmark.BookmarkId;
import com.hiketrackbackend.hiketrackbackend.model.country.Continent;
import com.hiketrackbackend.hiketrackbackend.model.country.Country;
import com.hiketrackbackend.hiketrackbackend.model.tour.Difficulty;
import com.hiketrackbackend.hiketrackbackend.model.user.User;
import com.hiketrackbackend.hiketrackbackend.model.tour.Tour;
import com.hiketrackbackend.hiketrackbackend.repository.country.CountryRepository;
import com.hiketrackbackend.hiketrackbackend.repository.tour.TourRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookmarkRepositoryTest {
    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Autowired
    private TourRepository tourRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CountryRepository countryRepository;
    private User user;
    private Tour tour;
    private Country country;
    private Bookmark bookmark;
    private BookmarkId bookmarkId;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
        bookmarkRepository.deleteAll();
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
        tour.setDifficulty(Difficulty.Easy);
        tour.setMainPhoto("photo.jpg");
        tour.setCountry(country);
        tour.setUser(user);
        tour.setDeleted(false);
        tour = tourRepository.save(tour);

        bookmark = new Bookmark();
        bookmark.setUser(user);
        bookmark.setTour(tour);
        bookmark.setAddedAt(LocalDateTime.now());

        bookmarkId = new BookmarkId(user.getId(), tour.getId());
        bookmark.setId(bookmarkId);
    }

    @Test
    @DisplayName("Find bookmark by user id")
    public void testFindByUserIdWhenBookmarkExistsThenReturnBookmark() {
        bookmarkRepository.save(bookmark);

        Set<Bookmark> bookmarks = bookmarkRepository.findByUser_Id(user.getId());

        assertNotNull(bookmarks);
        assertFalse(bookmarks.isEmpty());
        assertTrue(bookmarks.contains(bookmark));
    }

    @Test
    @DisplayName("Find by user id with no bookmarks")
    public void testFindByUserIdWhenNoBookmarkExistsThenReturnEmptySet() {
        Set<Bookmark> bookmarks = bookmarkRepository.findByUser_Id(user.getId());

        assertNotNull(bookmarks);
        assertTrue(bookmarks.isEmpty());
    }

    @Test
    @DisplayName("Check if bookmark is exist")
    public void testExistsByIdWhenBookmarkExistsThenReturnTrue() {
        bookmarkRepository.save(bookmark);

        boolean exists = bookmarkRepository.existsById(bookmarkId);

        assertTrue(exists);
    }

    @Test
    @DisplayName("Check if bookmark is exist with negative result")
    public void testExistsByIdWhenNoBookmarkExistsThenReturnFalse() {
        boolean exists = bookmarkRepository.existsById(bookmarkId);

        assertFalse(exists);
    }
}
