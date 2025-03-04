package com.wheel.wheelPicker.service;

import com.wheel.wheelPicker.dto.UserCreateDto;
import com.wheel.wheelPicker.dto.UserDto;
import com.wheel.wheelPicker.dto.UserLoginDto;
import com.wheel.wheelPicker.mapper.UserMapper;
import com.wheel.wheelPicker.model.User;
import com.wheel.wheelPicker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public UserDto createUser(UserCreateDto userCreateDto) {
        User user = new User(
                userCreateDto.getUsername(),
                userCreateDto.getEmail(),
                passwordEncoder.encode(userCreateDto.getPassword())
        );

        User savedUser = userRepository.save(user);
        return UserMapper.userToDto(savedUser); // Convert saved entity to UserDto
    }

    public boolean LoginUser(UserLoginDto userLoginDto) {
        User user = userRepository.findByEmail(userLoginDto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return passwordEncoder.matches(userLoginDto.getPassword(), user.getPassword());
    }
}
