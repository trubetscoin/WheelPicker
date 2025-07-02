package com.wheelpicker.integration.filter;

import com.wheelpicker.BaseNoDatabaseTest;
import com.wheelpicker.component.JwtUtility;
import com.wheelpicker.model.Role;
import com.wheelpicker.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
class JwtFilterIntegrationTest extends BaseNoDatabaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtility jwtUtility;

    private static final String ENDPOINT = "/test/jwt";

    @Value("${jwtUtility.accessSecretKey}")
    private String testAccessSecret;

    @Test
    void requestWithValidToken_returnsOk() throws Exception {
        String email = "test@example.com";
        String token = jwtUtility.generateAccessToken(email);

        User mockUser = new User();
        mockUser.setEmail(email);
        mockUser.setPassword("pass");
        mockUser.setRoles(Set.of(Role.USER));
        mockUser.setIsEnabled(true);

        given(userRepository.findByEmail(email)).willReturn(Optional.of(mockUser));

        mockMvc.perform(get(ENDPOINT)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(email)));
    }

    @Test
    void requestWithInvalidToken_returnsUnauthorized() throws Exception {
        String token = "Bad JWT";

        mockMvc.perform(get(ENDPOINT)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("Invalid JWT")));
    }

    @Test
    void requestWithExpiredToken_returnsUnauthorized() throws Exception {
        String expiredToken = Jwts.builder()
                .setSubject("test@example.com")
                .setExpiration(new Date(System.currentTimeMillis() - 3600000)) // expired 1hr ago
                .signWith(SignatureAlgorithm.HS256, testAccessSecret)
                .compact();

        mockMvc.perform(get(ENDPOINT)
                .header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("JWT expired")));
    }

    @Test
    void requestWithBrokenHeaders_returnsUnauthorized() throws Exception {
        mockMvc.perform(get(ENDPOINT)
                .header("Garbage", "garbage-value"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("Invalid JWT header")));
    }

    @Test
    void requestWithoutToken_skipsUnauthorized() throws Exception {
        mockMvc.perform(get(ENDPOINT))
                .andExpect(status().isUnauthorized());
    }

}