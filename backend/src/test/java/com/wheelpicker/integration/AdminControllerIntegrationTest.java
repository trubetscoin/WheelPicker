package com.wheelpicker.integration;

import com.wheelpicker.BaseDatabaseTest;
import com.wheelpicker.controller.AdminController;
import com.wheelpicker.model.Role;
import com.wheelpicker.model.User;
import com.wheelpicker.repository.UserRepository;
import com.wheelpicker.service.AdminService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

// Used WebClient instead of TestRestTemplate as it does handle request headers.
// TestRestTemplate does not send HttpHeaders
// No @Transaction as TestRestTemplate runs in a separate transaction and needs to see the data
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
    private UserRepository userRepository;

    private static final String ENDPOINT = "/admin";

    @BeforeEach
    void setup() {
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:" + port + ENDPOINT)
                .build();

        populateUsers();
    }

    @AfterEach
    void tearUp() {
        userRepository.deleteAll();
    }

    /*
    @Test
    public void getUsers_ReturnsOkWith3Users() {
        String queryParam = "use";

        // Also need to think about setting JWT Bearer
        Map response = webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/users")
                        .queryParam("query", queryParam)
                        .build())
                .header("Origin", "http://localhost:3000")
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        assertNotNull(response);


    }
     */

    private void populateUsers() {
        userRepository.save(
                new User(
                        "user",
                        "qwerty@example.com",
                        "password123",
                        Role.ROLE_USER
                )
        );

        userRepository.save(
                new User(
                        "notauser",
                        "email@example.com",
                        "qwerty123",
                        Role.ROLE_USER
                )
        );

        userRepository.save(
                new User(
                        "testing",
                        "emailcontainguserword@example.com",
                        "zxccxz123321",
                        Role.ROLE_USER
                )
        );

        userRepository.save(
                new User(
                        "qwerty",
                        "defaultemail@example.com",
                        "user",
                        Role.ROLE_USER
                )
        );

    }
}