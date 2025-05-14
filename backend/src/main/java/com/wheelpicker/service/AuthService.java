package com.wheelpicker.service;

import com.wheelpicker.component.JwtUtility;
import com.wheelpicker.dto.JwtWithExpiryDto;
import com.wheelpicker.dto.TokenPairDto;
import com.wheelpicker.dto.UserRegisterDto;
import com.wheelpicker.dto.UserLoginDto;
import com.wheelpicker.model.User;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

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
        jwtUtility.getRefreshTokenFromDb(refreshToken);
        return jwtUtility.refreshAccessToken(refreshToken);
    }

    private TokenPairDto generateTokenPairAndSaveRefresh(User user) {
        String accessToken = jwtUtility.generateAccessToken(user.getEmail());
        JwtWithExpiryDto refreshToken = jwtUtility.generateRefreshToken(user.getEmail());
        jwtUtility.saveRefreshToken(refreshToken, user);
        return new TokenPairDto(accessToken, refreshToken);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void logoutUser(String token) {
        jwtUtility.deleteRefreshToken(token);
    }
}