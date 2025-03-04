package com.wheel.wheelPicker.mapper;

import com.wheel.wheelPicker.dto.UserCreateDto;
import com.wheel.wheelPicker.dto.UserDto;
import com.wheel.wheelPicker.model.User;

public class UserMapper {
    public static UserDto userToDto(User user) {
        return new UserDto(
                user.getUsername(),
                user.getEmail()
        );
    }

    /*
    public static User userCreateDtoToUser(UserCreateDto userCreateDto) {
        return new User(
                userCreateDto.getUsername(),
                userCreateDto.getEmail(),
                userCreateDto.getPassword()
        );
    }
    */
}
