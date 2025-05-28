package com.wheelpicker.unit;

import com.wheelpicker.dto.UserLoginDto;
import com.wheelpicker.dto.UserRegisterDto;
import com.wheelpicker.exceptionHandling.exception.CredentialsAlreadyExistsException;
import com.wheelpicker.model.Role;
import com.wheelpicker.model.User;
import com.wheelpicker.repository.UserRepository;
import com.wheelpicker.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void registerUser_shouldRegister() {
        UserRegisterDto registerDto = new UserRegisterDto("registeruser", "registeruser@example.com", "password123");

        when(userRepository.findByEmail(registerDto.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(registerDto.getUsername())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(registerDto.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User savedUser = userService.registerUser(registerDto);

        assertEquals(registerDto.getUsername(), savedUser.getUsername());
        assertEquals(registerDto.getEmail(), savedUser.getEmail());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertEquals(Role.ROLE_USER, savedUser.getRole());

        verify(userRepository).findByEmail(registerDto.getEmail());
        verify(userRepository).findByUsername(registerDto.getUsername());
        verify(passwordEncoder).encode(registerDto.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_shouldThrowEmailAlreadyExists() {
        UserRegisterDto registerDto = new UserRegisterDto("registeruser", "registeruser@example.com", "password123");

        when(userRepository.findByEmail(registerDto.getEmail())).thenReturn(Optional.of(new User()));

        CredentialsAlreadyExistsException e = assertThrows(CredentialsAlreadyExistsException.class, () -> userService.registerUser(registerDto));
        assertEquals("email", e.getCredentialType());
        assertEquals(registerDto.getEmail(), e.getCredentialValue());

        verify(userRepository).findByEmail(registerDto.getEmail());
        verify(userRepository, never()).findByUsername(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser_shouldThrowUserAlreadyExists() {
        UserRegisterDto registerDto = new UserRegisterDto("registeruser", "registeruser@example.com", "password123");

        when(userRepository.findByEmail(registerDto.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(registerDto.getUsername())).thenReturn(Optional.of(new User()));

        CredentialsAlreadyExistsException e = assertThrows(CredentialsAlreadyExistsException.class, () -> userService.registerUser(registerDto));
        assertEquals("username", e.getCredentialType());
        assertEquals(registerDto.getUsername(), e.getCredentialValue());

        verify(userRepository).findByEmail(registerDto.getEmail());
        verify(userRepository).findByUsername(registerDto.getUsername());
        verify(userRepository, never()).save(any());
    }

    @Test
    void loginUser_shouldLogin() {
        UserLoginDto loginDto = new UserLoginDto("loginuser@example.com", "password123");

        User expectedUser = new User();
        expectedUser.setEmail("loginuser@example.com");

        UsernamePasswordAuthenticationToken authResult = new UsernamePasswordAuthenticationToken(expectedUser, null, null);

        when(authenticationManager.authenticate(any())).thenReturn(authResult);

        User loggedUser = userService.loginUser(loginDto);

        assertSame(expectedUser, loggedUser);
        assertSame(authResult, SecurityContextHolder.getContext().getAuthentication());
        assertEquals(loginDto.getEmail(), loggedUser.getEmail());
    }

    @Test
    void loginUser_shouldThrowBadCredentials() {
        UserLoginDto dto = new UserLoginDto("loginuser@example.com", "wrongpassword");

        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Invalid login or password"));

        BadCredentialsException e = assertThrows(BadCredentialsException.class, () -> userService.loginUser(dto));

        assertEquals("Invalid login or password", e.getMessage());
    }

}
