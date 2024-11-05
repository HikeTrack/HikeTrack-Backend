package com.hiketrackbackend.hiketrackbackend.repository;

import com.hiketrackbackend.hiketrackbackend.model.user.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = "userProfile")
    Optional<User> findById(Long id);

    @EntityGraph(attributePaths = {"roles", "userProfile"})
    Optional<User> findByEmail(String email);

    boolean existsUserByEmail(String email);
}
