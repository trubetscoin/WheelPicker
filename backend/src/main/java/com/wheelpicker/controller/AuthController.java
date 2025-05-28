package com.wheelpicker.controller;

import com.wheelpicker.dto.*;
import com.wheelpicker.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody @Valid UserRegisterDto userRegisterDto, HttpServletResponse response) {
        TokenPairDto jwtTokenPair = authService.register(userRegisterDto);
        setRefreshTokenCookie(response, jwtTokenPair.getRefreshToken());

        ApiSuccessResponseDto<Map<String, String>> success = new ApiSuccessResponseDto<>(
                HttpStatus.CREATED.value(),
                "Successfully created the user",
                Map.of("accessToken", jwtTokenPair.getAccessToken())
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(success);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody @Valid UserLoginDto userLoginDto, HttpServletResponse response) {
        TokenPairDto jwtTokenPair = authService.login(userLoginDto);
        setRefreshTokenCookie(response, jwtTokenPair.getRefreshToken());

        ApiSuccessResponseDto<Map<String, String>> success = new ApiSuccessResponseDto<>(
                HttpStatus.OK.value(),
                "Successfully logged in",
                Map.of("accessToken", jwtTokenPair.getAccessToken())
        );

        return ResponseEntity.ok(success);
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@CookieValue(name = "refreshToken") String token, HttpServletResponse response) {
        authService.logout(token);
        clearRefreshTokenCookie(response);

        ApiSuccessResponseDto<String> success = new ApiSuccessResponseDto<>(
                HttpStatus.OK.value(),
                "Logged out successfully",
                null
        );

        return ResponseEntity.ok(success);
    }

    @PostMapping("/refresh")
    ResponseEntity<?> getNewAccessToken(@CookieValue(name = "refreshToken") String token) {
        String newAccessToken = authService.getNewAccessToken(token);

        ApiSuccessResponseDto<Map<String, String>> success = new ApiSuccessResponseDto<>(
                HttpStatus.OK.value(),
                "Successfully refreshed the token",
                Map.of("accessToken", newAccessToken)
        );

        return ResponseEntity.ok(success);
    }

    public void setRefreshTokenCookie(HttpServletResponse response, JwtWithExpiryDto token) {
        Duration duration = Duration.between(LocalDateTime.now(), token.getExpiryDate());
        addRefreshTokenCookie(response, token.getToken(), duration);
    }

    public void clearRefreshTokenCookie(HttpServletResponse response) {
        addRefreshTokenCookie(response, "", Duration.ZERO);
    }

    private void addRefreshTokenCookie(HttpServletResponse response, String token, Duration maxAge){
        ResponseCookie cookie = ResponseCookie.from("refreshToken", token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(maxAge)
                .sameSite("None") // OriginCheckFilter checks the origin. No security concerns.
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

}