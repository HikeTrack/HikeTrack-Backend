package com.hiketrackbackend.hiketrackbackend.repository;

import com.hiketrackbackend.hiketrackbackend.model.user.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
}
