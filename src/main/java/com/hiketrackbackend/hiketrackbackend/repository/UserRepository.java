package com.hiketrackbackend.hiketrackbackend.repository;

import com.hiketrackbackend.hiketrackbackend.model.user.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = "userProfile")
    Optional<User> findById(Long id);

    @EntityGraph(attributePaths = "roles")
    Optional<User> findByEmail(String email);

    boolean existsUserByEmail(String email);
}
