package com.hiketrackbackend.hiketrackbackend.repository.tour;

import com.hiketrackbackend.hiketrackbackend.model.tour.Tour;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface TourRepository extends JpaRepository<Tour, Long>, JpaSpecificationExecutor<Tour> {
    @Query(value =
            "SELECT t.*, "
            + "AVG(r.rating) AS average_rating "
            + "FROM "
            + "tours t "
            + "LEFT JOIN "
            + "ratings r ON t.id = r.tour_id "
            + "GROUP BY "
            + "t.id "
            + "ORDER BY "
            + "average_rating DESC",
            nativeQuery = true)
    List<Tour> findTopToursWithHighestRatings(Pageable pageable);

    @Query("SELECT t FROM Tour t "
            + "LEFT JOIN FETCH t.tourDetails td "
            + "LEFT JOIN FETCH td.additionalPhotos "
            + "LEFT JOIN FETCH t.ratings r "
            + "WHERE t.id = :id")
    Optional<Tour> findTourById(Long id);

    boolean existsTourByUserIdAndName(Long userId, String name);

    @EntityGraph(attributePaths = "tourDetails")
    Optional<Tour> findTourByIdAndUserId(Long id, Long userId);

    boolean existsByIdAndUserId(Long id, Long userId);

    List<Tour> findAllTourByUserId(Long id, Pageable pageable);
}
