package com.wheelpicker.unit;

import com.wheelpicker.dto.UserDto;
import com.wheelpicker.exceptionHandling.exception.UserNotFoundException;
import com.wheelpicker.model.Role;
import com.wheelpicker.model.User;
import com.wheelpicker.repository.UserRepository;
import com.wheelpicker.service.AdminService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {

    // Spy instead of Mock, because Mock can't mock "default" methods
    @Spy
    private UserRepository userRepository;

    @InjectMocks
    private AdminService adminService;

    @Test
    void findUsers_shouldFindAllUsers() {

        String query = "test";

        User user1 = new User(
                "testuser",
                "email1@example.com",
                "password",
                Role.USER
        );

        User user2 = new User(
                "test",
                "email2@example.com",
                "password",
                Role.USER
        );

        when(userRepository.findByEmailOrUsername(query)).thenReturn(List.of(user1, user2));

        List<UserDto> result = adminService.findUsers(query);

        assertNotNull(result);

        List<String> usernames = result.stream()
                .map(UserDto::getUsername)
                .toList();

        assertEquals(2, result.size());
        assertTrue(usernames.contains("testuser"));
        assertTrue(usernames.contains("test"));

        verify(userRepository).findByEmailOrUsername(query);

    }

    @Test
    void findUsers_shouldReturnAllUsersWhenQueryIsNull() {

        String query = null;

        User user1 = new User(
                "testuser",
                "email1@example.com",
                "password",
                Role.USER
        );

        User user2 = new User(
                "anotheruser",
                "email2@example.com",
                "password",
                Role.USER
        );

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<UserDto> result = adminService.findUsers(query);

        assertNotNull(result);

        List<String> usernames = result.stream()
                .map(UserDto::getUsername)
                .toList();

        assertEquals(2, result.size());
        assertTrue(usernames.contains("testuser"));
        assertTrue(usernames.contains("anotheruser"));

        verify(userRepository).findAll();
    }

    @Test
    void findUsers_shouldReturnEmptyListWhenNoUsersFound() {

        String query = "test@example.com";

        when(userRepository.findByEmailOrUsername(query)).thenReturn(Collections.emptyList());

        List<UserDto> result = adminService.findUsers(query);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void banUser_shouldBan() {

        User mockedUser = new User(
                "testuser",
                "email@example.com",
                "password123",
                Role.USER
        );

        UUID mockedUserId = UUID.randomUUID();

        when(userRepository.findById(mockedUserId)).thenReturn(Optional.of(mockedUser));
        when(userRepository.save(mockedUser)).thenReturn(mockedUser);

        String email = adminService.banUser(mockedUserId);

        assertFalse(mockedUser.isEnabled());
        assertEquals("email@example.com", email);

        verify(userRepository).save(mockedUser);
    }

    @Test
    void banUser_shouldThrowWhenNoUserUUIDProvided() {

        User mockedUser = new User(
                "testuser",
                "email@example.com",
                "password123",
                Role.USER
        );

        UUID invalidUserId = UUID.randomUUID();

        when(userRepository.findById(invalidUserId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> adminService.banUser(invalidUserId));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void unbanUser_shouldUnban() {

        User mockedUser = new User(
                "testuser",
                "email@example.com",
                "password123",
                Role.USER
        );

        mockedUser.setIsEnabled(false);

        UUID mockedUserId = UUID.randomUUID();

        when(userRepository.findById(mockedUserId)).thenReturn(Optional.of(mockedUser));
        when(userRepository.save(mockedUser)).thenReturn(mockedUser);

        String email = adminService.unbanUser(mockedUserId);

        assertTrue(mockedUser.isEnabled());
        assertEquals("email@example.com", email);

        verify(userRepository).save(mockedUser);
    }

    @Test
    void unbanUser_shouldThrowWhenNoUserUUIDProvided() {


        User mockedUser = new User(
                "testuser",
                "email@example.com",
                "password123",
                Role.USER
        );

        UUID invalidUserId = UUID.randomUUID();

        when(userRepository.findById(invalidUserId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> adminService.unbanUser(invalidUserId));

        verify(userRepository, never()).save(any(User.class));
    }
}



