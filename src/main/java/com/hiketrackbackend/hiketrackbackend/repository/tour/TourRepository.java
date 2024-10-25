package com.hiketrackbackend.hiketrackbackend.repository.tour;

import com.hiketrackbackend.hiketrackbackend.model.tour.Tour;
import com.hiketrackbackend.hiketrackbackend.model.tour.details.TourDetails;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TourRepository extends JpaRepository<Tour, Long>, JpaSpecificationExecutor<Tour> {
    @EntityGraph(attributePaths = "user")
    List<Tour> findTop7ByRatingGreaterThanOrderByRatingDesc(int rating);
//
//    @EntityGraph(attributePaths = {"tourDetails", "additionalPhotos"})
//    Optional<Tour> findById(Long id);

    @Query("SELECT t FROM Tour t " +
            "LEFT JOIN FETCH t.tourDetails td " +
            "LEFT JOIN FETCH td.additionalPhotos tp " +
            "WHERE t.id = :id")
    Optional<Tour> findTourByIdAndTourDetailsWithAdditionalPhotos(Long id);

    boolean existsTourByUserIdAndName(Long userId,String name);

    Optional<Tour> findTourByReviewsId(Long id);

    @EntityGraph(attributePaths = "tourDetails")
    Optional<Tour> findTourByIdAndUserId(Long id, Long userId);

    boolean existsByIdAndUserId(Long id, Long userId);
}
