package com.moviebookingapp.movie_service.repository;

import com.moviebookingapp.movie_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Handles logging in with either Email, Login ID, or Contact Number
    @Query("SELECT u FROM User u WHERE u.email = :identifier OR u.username = :identifier OR u.contactNumber = :identifier")
    Optional<User> findByLoginIdentifier(@Param("identifier") String identifier);

    // These will be used during Registration (US_01) to ensure uniqueness
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByContactNumber(String contactNumber);
}
