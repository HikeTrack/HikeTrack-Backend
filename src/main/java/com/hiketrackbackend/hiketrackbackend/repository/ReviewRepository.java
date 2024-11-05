package com.hiketrackbackend.hiketrackbackend.repository;

import com.hiketrackbackend.hiketrackbackend.model.tour.Review;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByTourId(Long id, Pageable pageable);

    boolean existsByIdAndTourId(Long id, Long tourId);

    List<Review> findReviewsByUserId(Long tourId, Pageable pageable);
}
