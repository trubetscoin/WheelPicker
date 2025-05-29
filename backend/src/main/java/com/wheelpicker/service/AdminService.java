package com.wheelpicker.service;

import com.wheelpicker.dto.UserDto;
import com.wheelpicker.exceptionHandling.exception.UserNotFoundException;
import com.wheelpicker.model.User;
import com.wheelpicker.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AdminService {

    private final UserRepository userRepository;

    public AdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDto> findUsers(String query) {
        return userRepository.findByEmailOrUsername(query)
                .stream()
                .map(UserDto::new)
                .toList();
    }

    @Transactional
    public String banUser(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId.toString()));
        user.setIsEnabled(false);
        userRepository.save(user);
        return user.getEmail();
    }

    @Transactional
    public String unbanUser(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId.toString()));
        user.setIsEnabled(true);
        userRepository.save(user);
        return user.getEmail();
    }
}
