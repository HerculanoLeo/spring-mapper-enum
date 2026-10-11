package com.herculanoleo.spring.me.sample.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
class PersonOpenApiTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("Should publish Person.status as the mapped codes A and I")
    @Test
    void publishesMappedStatusCodes() throws Exception {
        var response = restTemplate.getForEntity("/v3/api-docs", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        var status = objectMapper.readTree(response.getBody())
                .path("components")
                .path("schemas")
                .path("Person")
                .path("properties")
                .path("status")
                .path("enum");

        assertEquals(List.of("A", "I"), objectMapper.convertValue(status, objectMapper.getTypeFactory().constructCollectionType(List.class, String.class)));
    }

}
