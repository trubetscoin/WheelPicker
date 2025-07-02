package com.wheelpicker.dto;

import com.wheelpicker.model.Role;
import com.wheelpicker.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class UserDto {
    private final UUID id;
    private final String username;
    private final String email;
    private final Set<Role> roles;
    private final Boolean isEnabled;

    public UserDto(User user) {
        this (
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRoles(),
                user.getIsEnabled()
        );
    }

}
