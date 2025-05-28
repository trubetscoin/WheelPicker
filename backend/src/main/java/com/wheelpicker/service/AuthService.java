package com.wheelpicker.service;

import com.wheelpicker.component.JwtUtility;
import com.wheelpicker.dto.JwtWithExpiryDto;
import com.wheelpicker.dto.TokenPairDto;
import com.wheelpicker.dto.UserLoginDto;
import com.wheelpicker.dto.UserRegisterDto;
import com.wheelpicker.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserService userService;
    private final JwtUtility jwtUtility;
    private final RefreshTokenService refreshTokenService;

    public AuthService(UserService userService, JwtUtility jwtUtility, RefreshTokenService refreshTokenService) {
        this.userService = userService;
        this.jwtUtility = jwtUtility;
        this.refreshTokenService = refreshTokenService;
    }

    public TokenPairDto register(UserRegisterDto userRegisterDto) {
        User user = userService.registerUser(userRegisterDto);
        return generateTokenPairAndSaveRefresh(user);
    }

    public TokenPairDto login(UserLoginDto userLoginDto) {
        User user = userService.loginUser(userLoginDto);
        return generateTokenPairAndSaveRefresh(user);
    }

    public String getNewAccessToken(String refreshToken) {
        refreshTokenService.getRefreshTokenFromDb(refreshToken);
        return jwtUtility.refreshAccessToken(refreshToken);
    }

    private TokenPairDto generateTokenPairAndSaveRefresh(User user) {
        String accessToken = jwtUtility.generateAccessToken(user.getEmail());
        JwtWithExpiryDto refreshToken = jwtUtility.generateRefreshToken(user.getEmail());
        refreshTokenService.saveRefreshToken(refreshToken, user);
        return new TokenPairDto(accessToken, refreshToken);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void logout(String token) {
        refreshTokenService.deleteRefreshToken(token);
    }
}