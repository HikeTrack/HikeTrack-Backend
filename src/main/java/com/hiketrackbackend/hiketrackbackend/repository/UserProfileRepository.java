package com.hiketrackbackend.hiketrackbackend.repository;

import com.hiketrackbackend.hiketrackbackend.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByUserId(Long userId);
}
