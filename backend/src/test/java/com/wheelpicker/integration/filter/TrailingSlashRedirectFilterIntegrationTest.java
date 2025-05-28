package com.wheelpicker.integration.filter;

import com.wheelpicker.BaseNoDatabaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TrailingSlashRedirectFilterIntegrationTest extends BaseNoDatabaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void requestWithTrailingSlash_shouldRedirect() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/test/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/test"));
    }

    @Test
    void requestWithQuery_shouldRedirectPreservingQuery() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/test/?param=value"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/test?param=value"));
    }
}