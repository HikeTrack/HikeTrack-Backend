package com.hiketrackbackend.hiketrackbackend.repository;

import com.hiketrackbackend.hiketrackbackend.model.tour.details.TourDetailsFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface TourDetailsFileRepository extends JpaRepository<TourDetailsFile, Long> {
    @Query("SELECT t FROM TourDetailsFile t WHERE t.tourDetailsId = :id")
    Optional<TourDetailsFile> findFirstByTourDetailsId(@Param("id") Long id);
}
