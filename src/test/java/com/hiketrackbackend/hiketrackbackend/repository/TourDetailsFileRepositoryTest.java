package com.hiketrackbackend.hiketrackbackend.repository;

import com.hiketrackbackend.hiketrackbackend.model.tour.details.TourDetailsFile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TourDetailsFileRepositoryTest {
    @Autowired
    private TourDetailsFileRepository tourDetailsRepository;

    @Test
    @DisplayName("Find tour files by tour detail id success")
    @Sql(scripts = "classpath:database/tour/add-tour-details-file.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void testFindByTourDetailsIdSuccess() {
        Set<TourDetailsFile> result = tourDetailsRepository.findByTourDetailsId(20L);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(file -> file.getFileUrl().equals("link1")));
        assertTrue(result.stream().anyMatch(file -> file.getFileUrl().equals("link2")));
    }

    @Test
    @DisplayName("Find all photos by valid id with not exist value")
    void testFindByTourDetailsIdWhenIdIsValidButNotExistEmptySet() {
        Set<TourDetailsFile> result = tourDetailsRepository.findByTourDetailsId(99999L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Find all photos with null detail Id")
    void testFindByTourDetailsIdWhenDetailIdNullEmptySet() {
        Set<TourDetailsFile> result = tourDetailsRepository.findByTourDetailsId(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
