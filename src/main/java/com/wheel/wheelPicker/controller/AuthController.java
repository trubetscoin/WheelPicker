package com.wheel.wheelPicker.controller;

import com.wheel.wheelPicker.dto.JwtWithExpiryDto;
import com.wheel.wheelPicker.dto.TokenPairDto;
import com.wheel.wheelPicker.dto.UserRegisterDto;
import com.wheel.wheelPicker.dto.UserLoginDto;
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
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(jwtTokenPair.getAccessToken());
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody @Valid UserLoginDto userLoginDto, HttpServletResponse response) {
        TokenPairDto jwtTokenPair = authService.login(userLoginDto);
        setRefreshTokenCookie(response, jwtTokenPair.getRefreshToken());
        return ResponseEntity.ok(jwtTokenPair.getAccessToken());
    }

    // doesn't work currently
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@CookieValue(name = "refreshToken") String token) {
        authService.logoutUser(token);
        return ResponseEntity.ok("Logged out successfully");
    }

    @PostMapping("/refresh")
    ResponseEntity<?> getNewAccessToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getRefreshTokenCookie(request);
        String newAccessToken = authService.getNewAccessToken(refreshToken);
        return ResponseEntity.ok(newAccessToken);
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

}