package com.hiketrackbackend.hiketrackbackend.repository;

import com.hiketrackbackend.hiketrackbackend.model.tour.details.TourDetailsFile;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TourDetailsFileRepository extends JpaRepository<TourDetailsFile, Long> {
    Set<TourDetailsFile> findByTourDetailsId(Long tourDetailsId);
}
