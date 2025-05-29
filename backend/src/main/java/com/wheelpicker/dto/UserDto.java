package com.wheelpicker.dto;

import com.wheelpicker.model.Role;
import com.wheelpicker.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class UserDto {
    private final UUID id;
    private final String username;
    private final String email;
    private final Role role;
    private final Boolean Enabled;

    public UserDto(User user) {
        this (
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getIsEnabled()
        );
    }

}
