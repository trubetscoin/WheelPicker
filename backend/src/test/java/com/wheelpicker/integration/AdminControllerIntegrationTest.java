package com.wheelpicker.integration;

import com.wheelpicker.BaseDatabaseTest;
import com.wheelpicker.controller.AdminController;
import com.wheelpicker.dto.ApiSuccessResponseDto;
import com.wheelpicker.dto.UserDto;
import com.wheelpicker.dto.UserLoginDto;
import com.wheelpicker.dto.UserRegisterDto;
import com.wheelpicker.model.Role;
import com.wheelpicker.model.User;
import com.wheelpicker.repository.UserRepository;
import com.wheelpicker.service.AdminService;
import com.wheelpicker.service.AuthService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

// Used WebClient instead of TestRestTemplate as it does handle request headers.
// No @Transaction as WebClient runs in a separate transaction and needs to see the data
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // webEnvironment is required for WebClient
@ActiveProfiles({"test", "integration"})
public class AdminControllerIntegrationTest extends BaseDatabaseTest {

    @LocalServerPort
    private int port;

    private WebClient webClient;

    @Autowired
    private AdminController adminController;

    @Autowired
    private AdminService adminService;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    private static final String ENDPOINT = "/admin";
    private static final String BAN_PATH = "/ban/";
    private static final String UNBAN_PATH = "/unban/";
    private String adminJWT;

    @BeforeEach
    void setup() {
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:" + port + ENDPOINT)
                .build();

        adminJWT = populateUsersAndGetAdminJWT();
    }

    @AfterEach
    void tearUp() {
        userRepository.deleteAll();
    }

    @Test
    public void getUsersWithQueryUser_ReturnsOkWithThreeUsers() {
        ApiSuccessResponseDto<List<UserDto>> response = getUsers("user");

        List<UserDto> users = response.getData();

        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("Successfully retrieved the requested users", response.getMessage());
        assertEquals(3, users.size());
        assertEquals("user", users.get(0).getUsername());
        assertEquals("qwerty@example.com", users.get(0).getEmail());
        assertEquals("notauser", users.get(1).getUsername());
        assertEquals("email@example.com", users.get(1).getEmail());
        assertEquals("testing", users.get(2).getUsername());
        assertEquals("emailcontainguserword@example.com", users.get(2).getEmail());
    }

    @Test
    public void getUsersWithQueryNonExistent_ReturnsOkWithZeroUsers() {
        ApiSuccessResponseDto<List<UserDto>> response = getUsers("non-existent");

        List<UserDto> users = response.getData();

        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("Successfully retrieved the requested users", response.getMessage());
        assertEquals(0, users.size());
    }

    @Test
    public void getUsersWithAdminRoleAndUppercaseData_ReturnsOkWithOneUser() {
        ApiSuccessResponseDto<List<UserDto>> response = getUsers("admin"); // lowercase query, however user's login and email are in uppercase

        List<UserDto> users = response.getData();

        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("Successfully retrieved the requested users", response.getMessage());
        assertEquals(1, users.size());
        assertEquals("ADMIN", users.get(0).getUsername());
        assertEquals("ADMIN@example.com", users.get(0).getEmail());
    }

    @Test
    public void getUsersWithEmptyQuery_ReturnsOkWithAllUsers() {
        ApiSuccessResponseDto<List<UserDto>> response = getUsers("");

        List<UserDto> users = response.getData();

        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("Successfully retrieved the requested users", response.getMessage());
        assertEquals(5, users.size());
    }

    @Test
    public void getUsersWithNoQuery_ReturnsOkWithAllUsers() {
        ApiSuccessResponseDto<List<UserDto>> response = getUsers(null);

        List<UserDto> users = response.getData();

        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("Successfully retrieved the requested users", response.getMessage());
        assertEquals(5, users.size());
    }

    @Test
    public void banExistingUser_ReturnsOk() {
        String email = "qwerty@example.com";
        UUID userId = userRepository.findByEmailOrThrow(email).getId();

        ApiSuccessResponseDto<String> response = (ApiSuccessResponseDto<String>) banOrUnbanUser(userId, BAN_PATH);
        User bannedUser = userRepository.findByEmailOrThrow(email);

        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("Successfully banned the user", response.getMessage());
        assertEquals(email, response.getData());
        assertFalse(bannedUser.isEnabled());
    }

    @Test
    public void banAlreadyBannedUser_ReturnsConflict() {
        String email = "qwerty@example.com";
        User userToBan = userRepository.findByEmailOrThrow(email);
        userToBan.setIsEnabled(false);
        userRepository.save(userToBan);

        UUID userId = userToBan.getId();

        ProblemDetail response = (ProblemDetail) banOrUnbanUser(userId, BAN_PATH);
        User bannedUser = userRepository.findByEmailOrThrow(email);

        assertNotNull(response);
        assertEquals(409, response.getStatus());
        assertEquals("User cannot be banned", response.getTitle());
        assertEquals("User is already banned", response.getDetail());
        assertEquals("BAN_CONFLICT", response.getProperties().get("errorCode"));
        assertFalse(bannedUser.isEnabled());
    }

    @Test
    public void banAdminUser_ReturnsConflict() {
        String email = "ADMIN@example.com";
        UUID userId = userRepository.findByEmailOrThrow(email).getId();

        ProblemDetail response = (ProblemDetail) banOrUnbanUser(userId, BAN_PATH);
        User admin = userRepository.findByEmailOrThrow(email);

        assertNotNull(response);
        assertEquals(409, response.getStatus());
        assertEquals("User cannot be banned", response.getTitle());
        assertEquals("User is an admin and cannot be banned", response.getDetail());
        assertEquals("BAN_CONFLICT", response.getProperties().get("errorCode"));
        assertTrue(admin.isEnabled());
    }

    @Test
    public void banNonExistentUser_ReturnsConflict() {
        UUID userId = UUID.randomUUID();

        ProblemDetail response = (ProblemDetail) banOrUnbanUser(userId, BAN_PATH);

        assertNotNull(response);
        assertEquals(404, response.getStatus());
        assertEquals("User could not be found", response.getTitle());
        assertEquals("User " + userId + " could not be found", response.getDetail());
        assertEquals("RESOURCE_NOT_FOUND", response.getProperties().get("errorCode"));
    }

    @Test
    public void unbanExistingUser_ReturnsOk() {
        String email = "defaultemail@example.com";
        UUID userId = userRepository.findByEmailOrThrow(email).getId();

        ApiSuccessResponseDto<String> response = (ApiSuccessResponseDto<String>) banOrUnbanUser(userId, UNBAN_PATH);
        User unbannedUser = userRepository.findByEmailOrThrow(email);

        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("Successfully unbanned the user", response.getMessage());
        assertEquals(email, response.getData());
        assertTrue(unbannedUser.isEnabled());
    }

    @Test
    public void unbanAlreadyUnbannedUser_ReturnsOk() {
        String email = "qwerty@example.com";
        UUID userId = userRepository.findByEmailOrThrow(email).getId();

        ProblemDetail response = (ProblemDetail) banOrUnbanUser(userId, UNBAN_PATH);
        User unbannedUser = userRepository.findByEmailOrThrow(email);

        assertNotNull(response);
        assertEquals(409, response.getStatus());
        assertEquals("User cannot be unbanned", response.getTitle());
        assertEquals("BAN_CONFLICT", response.getProperties().get("errorCode"));
        assertTrue(unbannedUser.isEnabled());
    }

    @Test
    public void unbanAdminUser_ReturnsConflict() {
        String email = "ADMIN@example.com";
        UUID userId = userRepository.findByEmailOrThrow(email).getId();

        ProblemDetail response = (ProblemDetail) banOrUnbanUser(userId, UNBAN_PATH);
        User admin = userRepository.findByEmailOrThrow(email);

        assertNotNull(response);
        assertEquals(409, response.getStatus());
        assertEquals("User cannot be unbanned", response.getTitle());
        assertEquals("User is an admin and cannot be unbanned", response.getDetail());
        assertEquals("BAN_CONFLICT", response.getProperties().get("errorCode"));
        assertTrue(admin.isEnabled());
    }

    @Test
    public void unbanNonExistentUser_ReturnsConflict() {
        UUID userId = UUID.randomUUID();

        ProblemDetail response = (ProblemDetail) banOrUnbanUser(userId, UNBAN_PATH);

        assertNotNull(response);
        assertEquals(404, response.getStatus());
        assertEquals("User could not be found", response.getTitle());
        assertEquals("User " + userId + " could not be found", response.getDetail());
        assertEquals("RESOURCE_NOT_FOUND", response.getProperties().get("errorCode"));
    }

    private Object banOrUnbanUser(UUID userId, String path) {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path(path + userId).build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminJWT)
                .header(HttpHeaders.ORIGIN, "http://localhost:3000")
                .exchangeToMono(response -> {
                    if (response.statusCode() == HttpStatus.OK) {
                        return response.bodyToMono(new ParameterizedTypeReference<ApiSuccessResponseDto<String>>() {})
                                .cast(Object.class);
                    } else {
                        return response.bodyToMono(ProblemDetail.class)
                                .cast(Object.class);
                    }
                })
                .block();
    }

    private ApiSuccessResponseDto<List<UserDto>> getUsers(String query) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/users")
                        .queryParam("query", query)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminJWT)
                .header(HttpHeaders.ORIGIN, "http://localhost:3000")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiSuccessResponseDto<List<UserDto>>>() {})
                .block();
    }

    private String populateUsersAndGetAdminJWT() {
        authService.register(
                new UserRegisterDto(
                        "user",
                        "qwerty@example.com",
                        "password123"
                )
        );

        authService.register(
                new UserRegisterDto(
                        "notauser",
                        "email@example.com",
                        "qwerty123"
                )
        );

        authService.register(
                new UserRegisterDto(
                        "testing",
                        "emailcontainguserword@example.com",
                        "zxccxz123321"
                )
        );

        authService.register(
                new UserRegisterDto(
                        "qwerty",
                        "defaultemail@example.com",
                        "user"
                )
        );

        authService.register(
                new UserRegisterDto(
                        "ADMIN",
                        "ADMIN@example.com",
                        "ADMIN PASSWORD"
                )
        );

        User admin = userRepository.findByEmail("ADMIN@example.com").get();
        admin.addRole(Role.ADMIN);
        userRepository.save(admin);

        User userToBeBanned = userRepository.findByEmail("defaultemail@example.com").get();
        userToBeBanned.setIsEnabled(false);
        userRepository.save(userToBeBanned);

        return authService.login(
                new UserLoginDto(
                        "ADMIN@example.com",
                        "ADMIN PASSWORD"
                )
        ).getAccessToken();
    }

}