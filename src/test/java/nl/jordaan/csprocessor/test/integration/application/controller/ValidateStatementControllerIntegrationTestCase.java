package nl.jordaan.csprocessor.test.integration.application.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import nl.jordaan.csprocessor.objectmodel.constant.ExecutionStatus;
import nl.jordaan.csprocessor.test.integration.BaseIntegrationTestCase;
import nl.jordaan.csprocessor.objectmodel.dto.GetExecutionDetailsResponse;
import nl.jordaan.csprocessor.objectmodel.dto.GetExecutionStatusResponse;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.*;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collections;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ValidateStatementControllerIntegrationTestCase extends BaseIntegrationTestCase {

    @Test
    @Order(1)
    public void testUploadCSVDocument() throws URISyntaxException {
        testUploadDocument("records.csv");
    }

    @Test
    @Order(2)
    public void testUploadXMLDocument() throws URISyntaxException {
        testUploadDocument("records.xml");
    }

    @Test
    @Order(3)
    public void testGetStatus_fail() {

        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/rest-api/v1/customer-statement/processing/validation-jobs/-1/status"),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );

        System.out.println(response.getBody());
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @Order(4)
    public void testGetStatus_pass() throws JsonProcessingException {

        headers.setContentType(MediaType.APPLICATION_JSON);

        LocalDateTime start = LocalDateTime.now();
        Awaitility.await().until(() -> LocalDateTime.now().isAfter(start.plusSeconds(5)));

        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/rest-api/v1/customer-statement/processing/validation-jobs/0/status"),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );

        System.out.println(response.getBody());
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        GetExecutionStatusResponse detailsObj = mapper.readValue(response.getBody(), GetExecutionStatusResponse.class);
        Assertions.assertNotNull(detailsObj);
        Assertions.assertEquals(0, detailsObj.getExecutionId());
        Assertions.assertEquals(ExecutionStatus.COMPLETED.name(), detailsObj.getExecutionStatus());
    }

    @Test
    @Order(5)
    public void testGetDetails_fail() {

        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/rest-api/v1/customer-statement/processing/validation-jobs/-1/details"),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );

        System.out.println(response.getBody());
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @Order(6)
    public void testGetDetails_pass() throws JsonProcessingException {

        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/rest-api/v1/customer-statement/processing/validation-jobs/0/details"),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );

        System.out.println(response.getBody());
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        GetExecutionDetailsResponse detailsObj = mapper.readValue(response.getBody(), GetExecutionDetailsResponse.class);
        Assertions.assertNotNull(detailsObj);
        Assertions.assertEquals(0, detailsObj.getExecutionId());
        Assertions.assertEquals(ExecutionStatus.COMPLETED.name(), detailsObj.getExecutionStatus());
    }

    @Test
    @Order(7)
    public void testDownloadResults_fail() {

        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/rest-api/v1/customer-statement/processing/validation-jobs/-1/results"),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );

        System.out.println(response.getBody());
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @Order(8)
    public void testDownloadResults_pass() {

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));

        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("/rest-api/v1/customer-statement/processing/validation-jobs/0/results"),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );

        System.out.println(response.getBody());
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    private void testUploadDocument(String fileName) throws URISyntaxException {

        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(Path.of(getClass().getClassLoader().getResource(fileName).toURI())));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response =
                restTemplate.postForEntity(
                        createURLWithPort("/rest-api/v1/customer-statement/processing/validation-jobs/upload"),
                        requestEntity,
                        String.class
                );

        System.out.println(response.getBody());
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

}
