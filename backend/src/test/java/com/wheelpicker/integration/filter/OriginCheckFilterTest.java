package com.wheelpicker.integration.filter;

import com.wheelpicker.BaseNoDatabaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
public class OriginCheckFilterTest extends BaseNoDatabaseTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String ENDPOINT = "/test/originCheck";

    @Test
    void requestWithAllowedOrigin_returnsOk() throws Exception {
        mockMvc.perform(get(ENDPOINT)
                .header("Origin", "http://localhost:3000"))
                .andExpect(status().isOk())
                .andExpect(content().string("http://localhost:3000"));
    }

    @Test
    void requestWithForbiddenOrigin_returnsForbidden() throws Exception {
        mockMvc.perform(get(ENDPOINT)
                        .header("Origin", "http://localhost:5000"))
                .andExpect(status().isForbidden())
                .andExpect(content().string(containsString("Forbidden Origin")));
    }

    @Test
    void requestWithNoOrigin_returnsForbidden() throws Exception {
        mockMvc.perform(get(ENDPOINT))
                .andExpect(status().isForbidden());
    }

}
