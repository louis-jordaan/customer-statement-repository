package nl.jordaan.csprocessor.test.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import nl.jordaan.csprocessor.application.CustomerStatementProcessorApplication;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = CustomerStatementProcessorApplication.class)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BaseIntegrationTestCase {

    @LocalServerPort
    protected int port;

    @Value("${auth.jwt.test-user}")
    private String username;
    @Value("${auth.jwt.test-password}")
    private String password;

    protected TestRestTemplate restTemplate;
    protected HttpHeaders headers;

    protected ObjectMapper mapper;

    @BeforeAll
    public void init() throws JsonProcessingException {
        restTemplate = new TestRestTemplate();
        mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);

        headers = new HttpHeaders();
        headers.add("Authorization", String.format("Bearer %s", getJwtToken()));
    }

    protected String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

    protected String getJwtToken() throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> credentialData = new HashMap<>();
        credentialData.put("username", username);
        credentialData.put("password", password);

        HttpEntity<String> request = new HttpEntity<>(mapper.writeValueAsString(credentialData), headers);

        ResponseEntity<String> response = restTemplate.postForEntity(createURLWithPort("/rest-api/v1/authentication/login"), request, String.class);
        if (response.getStatusCode() == HttpStatus.OK && response.getHeaders().containsKey("Authorization")) {
            return response.getHeaders().get("Authorization").get(0).replaceFirst("Bearer ", "");
        } else {
            throw new RuntimeException("Failed to acquire JWT token.");
        }
    }
}
