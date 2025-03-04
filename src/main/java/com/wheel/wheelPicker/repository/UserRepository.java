package com.wheel.wheelPicker.repository;

import com.wheel.wheelPicker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // custom queries

    Optional<User> findByEmail(String email);
}
