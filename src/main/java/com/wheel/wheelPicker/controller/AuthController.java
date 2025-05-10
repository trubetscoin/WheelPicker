package com.wheel.wheelPicker.controller;

import com.wheel.wheelPicker.dto.*;
import com.wheel.wheelPicker.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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
import java.util.Objects;


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
        authService.logoutUser(token);
        clearRefreshTokenCookie(response);

        ApiSuccessResponseDto<String> success = new ApiSuccessResponseDto<>(
                HttpStatus.OK.value(),
                "Logged out successfully",
                null
        );

        return ResponseEntity.ok(success);
    }

    @PostMapping("/refresh")
    ResponseEntity<?> getNewAccessToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getRefreshTokenCookie(request);
        String newAccessToken = authService.getNewAccessToken(refreshToken);

        ApiSuccessResponseDto<Map<String, String>> success = new ApiSuccessResponseDto<>(
                HttpStatus.OK.value(),
                "Successfully refresh the token",
                Map.of("accessToken", newAccessToken)
        );

        return ResponseEntity.ok(success);
    }

    public void setRefreshTokenCookie(HttpServletResponse response, JwtWithExpiryDto token){
        ResponseCookie cookie = ResponseCookie.from("refreshToken", token.getToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.between(LocalDateTime.now(), token.getExpiryDate()))
                .sameSite("None") // OriginCheckFilter checks the origin. No security concerns.
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public String getRefreshTokenCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (Objects.equals(cookie.getName(), "refreshToken")) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public void clearRefreshTokenCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("None")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

}