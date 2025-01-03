package com.hiketrackbackend.hiketrackbackend.repository;

import com.hiketrackbackend.hiketrackbackend.model.SocialSubscription;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SocialSubscriptionRepository extends JpaRepository<SocialSubscription, Long> {
    boolean existsByEmail(String email);

    void deleteByEmail(String email);

    @Query("SELECT s.email FROM SocialSubscription s")
    List<String> findAllEmails();
}
