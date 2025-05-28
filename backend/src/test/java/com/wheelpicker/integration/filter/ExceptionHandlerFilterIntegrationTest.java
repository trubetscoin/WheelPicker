package com.wheelpicker.integration.filter;

import com.wheelpicker.BaseNoDatabaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


// Full webEnvironment required for the class to use TestRestTemplate
// Otherwise the RuntimeException wrapped into ServletException
// From TestController in exceptionHandlerThrowsEndpoint
// Won't be actually redirected to CustomErrorController
// And hence the result of the test would be unsatisfiable

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration"}
)
public class ExceptionHandlerFilterIntegrationTest extends BaseNoDatabaseTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String ENDPOINT = "/test/exceptionHandler";

    @Test
    public void requestToOkEndpoint_returnsOk() throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity(ENDPOINT + "/ok", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("OK");
    }

    @Test
    public void requestToBadEndpoint_returnsInternalServerError() {
        ResponseEntity<String> response = restTemplate.getForEntity(ENDPOINT + "/throws", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON);
        assertThat(response.getBody()).contains("Simulated exception");
        assertThat(response.getBody()).contains("INTERNAL_SERVER_ERROR");
    }
}


