package com.hiketrackbackend.hiketrackbackend.repository;

import com.hiketrackbackend.hiketrackbackend.model.tour.details.TourDetailsFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TourDetailsFileRepository extends JpaRepository<TourDetailsFile, Long> {
}
