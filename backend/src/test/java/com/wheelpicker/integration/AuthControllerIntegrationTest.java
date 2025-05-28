package com.wheelpicker.integration;

import com.wheelpicker.BaseDatabaseTest;
import com.wheelpicker.controller.AuthController;
import com.wheelpicker.dto.UserRegisterDto;
import com.wheelpicker.repository.UserRepository;
import com.wheelpicker.service.AuthService;
import com.wheelpicker.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

// No @Transaction as TestRestTemplate runs in a separate transaction and needs to see the data
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // webEnvironment is required for TestRestTemplate
@ActiveProfiles({"test", "integration"})
public class AuthControllerIntegrationTest extends BaseDatabaseTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AuthController authController;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private static final String ENDPOINT = "/api/auth";
    private static final String EMAIL = "email@example.com";
    private static final String USERNAME = "testuser";
    private static final String PASSWORD = "password123";

    @AfterEach
    void TearUp() {
        if (userRepository.findByEmail(EMAIL).isPresent())
        {
            userRepository.delete(userRepository.findByEmail(EMAIL).get());
        }
    }

    @Test
    public void registerUser_ReturnsCreated() {
        Map<String, String> requestBody = Map.of(
                "email", EMAIL,
                "password", PASSWORD,
                "username", USERNAME
        );

        ResponseEntity<Map> response = restTemplate.postForEntity(
                ENDPOINT + "/register",
                requestBody,
                Map.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        Map body = response.getBody();

        assertEquals(201, body.get("status"));
        assertEquals("Successfully created the user", body.get("message"));
        assertNotNull(((Map) body.get("data")).get("accessToken"));

        assertTrue(response.getHeaders().get(HttpHeaders.SET_COOKIE)
                .stream().anyMatch(cookie -> cookie.startsWith("refreshToken=")));
    }

    @Test
    public void registerUserWithNoPasswordAndNoUsername_ReturnsBadRequest() {
        Map<String, String> requestBody = Map.of(
                "email", EMAIL
        );

        ResponseEntity<Map> response = restTemplate.postForEntity(
                ENDPOINT + "/register",
                requestBody,
                Map.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        Map body = response.getBody();

        assertEquals(400, body.get("status"));
        assertEquals("One or more fields are invalid", body.get("detail"));
        assertEquals("VALIDATION_FAILURE", body.get("errorCode"));
    }

    @Test
    public void loginUser_ReturnsOk() {

        userService.registerUser(new UserRegisterDto(
                USERNAME,
                EMAIL,
                PASSWORD
        ));

        Map<String, String> requestBody = Map.of(
                "email", EMAIL,
                "password", PASSWORD
        );

        ResponseEntity<Map> response = restTemplate.postForEntity(
                ENDPOINT + "/login",
                requestBody,
                Map.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map body = response.getBody();
        assertEquals(200, body.get("status"));
        assertEquals("Successfully logged in", body.get("message"));
        assertNotNull(((Map) body.get("data")).get("accessToken"));

        assertTrue(response.getHeaders().get(HttpHeaders.SET_COOKIE)
                .stream().anyMatch(cookie -> cookie.startsWith("refreshToken=")));
    }

    @Test
    public void loginUserWithNoPassword_ReturnsBadRequest() {

        userService.registerUser(new UserRegisterDto(
                USERNAME,
                EMAIL,
                PASSWORD
        ));

        Map<String, String> requestBody = Map.of(
                "email", EMAIL
        );

        ResponseEntity<Map> response = restTemplate.postForEntity(
                ENDPOINT + "/login",
                requestBody,
                Map.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        Map body = response.getBody();

        assertEquals(400, body.get("status"));
        assertEquals("One or more fields are invalid", body.get("detail"));
        assertEquals("VALIDATION_FAILURE", body.get("errorCode"));
    }

    @Test
    public void refresh_ReturnsOk() {
        Map<String, String> requestBody = Map.of(
                "email", EMAIL,
                "password", PASSWORD,
                "username", USERNAME
        );

        String refreshToken = authService
                .register(new UserRegisterDto(
                        USERNAME,
                        EMAIL,
                        PASSWORD))
                .getRefreshToken()
                .getToken();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, "refreshToken=" + refreshToken);

        HttpEntity<?> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                ENDPOINT + "/refresh",
                HttpMethod.POST,
                request,
                Map.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map body = response.getBody();
        assertEquals(200, body.get("status"));
        assertEquals("Successfully refreshed the token", body.get("message"));
        assertNotNull(((Map) body.get("data")).get("accessToken"));
    }

    @Test
    public void logoutUser_ReturnsOk() {
        String refreshToken = authService
                .register(new UserRegisterDto(
                        USERNAME,
                        EMAIL,
                        PASSWORD))
                .getRefreshToken()
                .getToken();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, "refreshToken=" + refreshToken);

        HttpEntity<?> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                ENDPOINT + "/logout",
                HttpMethod.POST,
                request,
                Map.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map body = response.getBody();
        assertEquals(200, body.get("status"));
        assertEquals("Logged out successfully", body.get("message"));
    }
}
