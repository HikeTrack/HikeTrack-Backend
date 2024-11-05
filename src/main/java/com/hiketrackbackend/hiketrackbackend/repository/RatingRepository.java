package com.hiketrackbackend.hiketrackbackend.repository;

import com.hiketrackbackend.hiketrackbackend.model.tour.Rating;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    Optional<Rating> findRatingByUser_IdAndTour_Id(Long userId, Long tourId);

    boolean existsByUserIdAndTourId(Long userId, Long tourId);
}
