package com.hiketrackbackend.hiketrackbackend.repository;

import com.hiketrackbackend.hiketrackbackend.model.SocialSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocialSubscriptionRepository extends JpaRepository<SocialSubscription, Long> {
    boolean existsByEmail(String email);

    void deleteByEmail(String email);
}
