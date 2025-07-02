package com.wheelpicker.integration.filter;

import com.wheelpicker.BaseDatabaseTest;
import com.wheelpicker.dto.UserLoginDto;
import com.wheelpicker.dto.UserRegisterDto;
import com.wheelpicker.model.User;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

// No @Transaction as TestRestTemplate runs in a separate transaction and needs to see the data
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // webEnvironment is required for TestRestTemplate
public class IsUserBannedFilterIntegrationTest extends BaseDatabaseTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private static final String ENDPOINT = "/test/isUserBanned";
    private static final String EMAIL = "email@example.com";
    private static final String USERNAME = "testuser";
    private static final String PASSWORD = "password123";

    @AfterEach
    void TearUp() {
        User user = userRepository.findByEmail(EMAIL).get();
        userRepository.delete(user);
    }

    @Test
    void requestWithEnabledUser_returnsOk() {
        registerUser();
        String accessToken = loginUserAndGetAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                ENDPOINT,
                HttpMethod.GET,
                entity,
                String.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void requestWithDisabledUser_returnsForbidden() throws Exception {
        User user = registerUser();
        String accessToken = loginUserAndGetAccessToken();

        user.setIsEnabled(false);
        userRepository.save(user);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                ENDPOINT,
                HttpMethod.GET,
                entity,
                String.class
        );
    }

    private User registerUser() {
        UserRegisterDto dto = new UserRegisterDto(
                USERNAME,
                EMAIL,
                PASSWORD
        );
        return userService.registerUser(dto);
    }

    private String loginUserAndGetAccessToken() {
        UserLoginDto dto = new UserLoginDto(
                EMAIL,
                PASSWORD
        );
        return authService.login(dto).getAccessToken();
    }
}
