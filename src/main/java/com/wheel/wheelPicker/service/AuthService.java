package com.wheel.wheelPicker.service;

import com.wheel.wheelPicker.component.JwtUtility;
import com.wheel.wheelPicker.dto.JwtWithExpiryDto;
import com.wheel.wheelPicker.dto.TokenPairDto;
import com.wheel.wheelPicker.dto.UserRegisterDto;
import com.wheel.wheelPicker.dto.UserLoginDto;
import com.wheel.wheelPicker.model.User;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserService userService;
    private final JwtUtility jwtUtility;

    public AuthService(UserService userService, JwtUtility jwtUtility) {
        this.userService = userService;
        this.jwtUtility = jwtUtility;
    }

    public TokenPairDto login(UserLoginDto userLoginDto) {
        User user = userService.loginUser(userLoginDto);
        return generateTokenPairAndSaveRefresh(user);
    }

    public TokenPairDto register(UserRegisterDto userRegisterDto) {
        User user = userService.registerUser(userRegisterDto);
        return generateTokenPairAndSaveRefresh(user);
    }

    public String getNewAccessToken(String refreshToken) {
        if (refreshToken == null) throw new BadCredentialsException("Refresh token is missing");
        else if (!jwtUtility.isRefreshTokenInDb(refreshToken)) throw new BadCredentialsException("Refresh token not recognized");
        return jwtUtility.refreshAccessToken(refreshToken);
    }

    private TokenPairDto generateTokenPairAndSaveRefresh(User user) {
        String accessToken = jwtUtility.generateAccessToken(user.getEmail());
        JwtWithExpiryDto refreshToken = jwtUtility.generateRefreshToken(user.getEmail());
        jwtUtility.saveRefreshToken(refreshToken, user);
        return new TokenPairDto(accessToken, refreshToken);
    }

    public void logoutUser(String authHeader) {
        if (!authHeader.startsWith("Bearer ")) throw new BadCredentialsException("Invalid token format");
        String token = authHeader.substring(7);
        jwtUtility.deleteRefreshToken(token); // doesn't work currently
    }
}