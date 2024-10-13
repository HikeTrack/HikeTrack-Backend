package com.hiketrackbackend.hiketrackbackend.repository.tour;

import com.hiketrackbackend.hiketrackbackend.model.tour.Tour;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;
import java.util.Optional;

public interface TourRepository extends JpaRepository<Tour, Long>, JpaSpecificationExecutor<Tour> {
    List<Tour> findTop7ByRatingGreaterThanOrderByRatingDesc(int rating);

    @EntityGraph(attributePaths = "details")
    Optional<Tour> findById(Long id);

    boolean existsTourByUserIdAndName(Long userId,String name);

    Optional<Tour> findTourByReviewsId(Long id);
}
