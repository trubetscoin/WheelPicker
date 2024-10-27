package com.wheel.wheelPicker.repository;

import com.wheel.wheelPicker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    // custom queries
}
