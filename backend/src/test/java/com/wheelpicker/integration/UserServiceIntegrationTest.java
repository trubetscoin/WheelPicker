package com.wheelpicker.integration;

import com.wheelpicker.dto.UserLoginDto;
import com.wheelpicker.dto.UserRegisterDto;
import com.wheelpicker.exceptionHandling.exception.CredentialsAlreadyExistsException;
import com.wheelpicker.model.Role;
import com.wheelpicker.model.User;
import com.wheelpicker.repository.UserRepository;
import com.wheelpicker.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldRegisterUserSuccessfully() {
        UserRegisterDto dto = new UserRegisterDto("registeruser", "registeruser@example.com", "password123");

        User registeredUser = userService.registerUser(dto);

        assertNotNull(registeredUser);
        assertEquals("registeruser", registeredUser.getUsername());
        assertEquals("registeruser@example.com", registeredUser.getEmail());
        assertTrue(passwordEncoder.matches("password123", registeredUser.getPassword()));
        assertEquals(Role.ROLE_USER, registeredUser.getRole());
    }

    @Test
    void shouldThrowExceptionForDuplicateEmailRegistration() {
        userRepository.save(new User("existinguser", "registeruser@example.com", "encodedPassword", Role.ROLE_USER));
        UserRegisterDto dto = new UserRegisterDto("registeruser", "registeruser@example.com", "password123");

        assertThrows(CredentialsAlreadyExistsException.class, () -> userService.registerUser(dto));
    }

    @Test
    void shouldThrowExceptionForDuplicateUsernameRegistration() {
        userRepository.save(new User("registeruser", "existinguser@example.com", "encodedPassword", Role.ROLE_USER));
        UserRegisterDto dto = new UserRegisterDto("registeruser", "registeruser@example.com", "password123");

        assertThrows(CredentialsAlreadyExistsException.class, () -> userService.registerUser(dto));
    }

    @Test
    void shouldAuthenticateUser() {
        UserRegisterDto registerDto = new UserRegisterDto("loginuser", "loginuser@example.com", "password123");
        userService.registerUser(registerDto);
        UserLoginDto loginDto = new UserLoginDto("loginuser@example.com", "password123");

        User loggedInUser = userService.loginUser(loginDto);

        assertEquals(registerDto.getEmail(), loggedInUser.getEmail());
        assertEquals(registerDto.getUsername(), loggedInUser.getUsername());
    }

    @Test
    void shouldThrowExceptionForInvalidLoginAuthentication() {
        UserRegisterDto registerDto = new UserRegisterDto("loginuser", "loginuser@example.com", "password123");
        userService.registerUser(registerDto);
        UserLoginDto invalidLoginDto = new UserLoginDto("loginuser@example.com", "wrongpassword");

        assertThrows(BadCredentialsException.class, () -> userService.loginUser(invalidLoginDto));
    }

    @Test
    void shouldThrowExceptionForInvalidPasswordAuthentication() {
        UserRegisterDto registerDto = new UserRegisterDto("loginuser", "loginuser@example.com", "password123");
        userService.registerUser(registerDto);
        UserLoginDto invalidLoginDto = new UserLoginDto("wrongemail@example.com", "password123");

        assertThrows(BadCredentialsException.class, () -> userService.loginUser(invalidLoginDto));
    }
}
