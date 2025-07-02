package com.wheelpicker.repository;

import com.wheelpicker.exceptionHandling.exception.UserNotFoundException;
import com.wheelpicker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    default User findByEmailOrThrow(String email) {
        return findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
    }

    default User findByIdOrThrow(UUID id) {
        return findById(id).orElseThrow(() -> new UserNotFoundException(id.toString()));
    }

    Optional<User> findByUsername(String username);

    // query should match the model entity, not table
    @Query("""
    SELECT u FROM User u
    WHERE LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))
       OR LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%'))
    """)
    List<User> findByEmailOrUsername(@Param("query") String query);
}
