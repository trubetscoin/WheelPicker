package com.wheelpicker.repository;

import com.wheelpicker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);

    // query should match the model entity, not table
    @Query("SELECT u FROM User u WHERE u.email LIKE CONCAT('%', :query, '%') OR u.username LIKE CONCAT('%', :query, '%')")
    List<User> findByEmailOrUsername(@Param("query") String query);
}
