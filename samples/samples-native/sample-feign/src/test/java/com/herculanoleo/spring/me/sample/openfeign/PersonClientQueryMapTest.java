package com.herculanoleo.spring.me.sample.openfeign;

import com.herculanoleo.spring.me.sample.models.dto.PersonSearchRequest;
import com.herculanoleo.spring.me.sample.models.enums.PersonStatus;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class PersonClientQueryMapTest {

    private static final String PERSON_JSON = """
            [{"id":"00000000-0000-0000-0000-000000000001","name":"Ada","birthdate":"1990-01-01","status":"A"}]
            """;

    private static HttpServer server;

    private static volatile String query;

    @DynamicPropertySource
    static void personClientUrl(DynamicPropertyRegistry registry) throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/person", exchange -> {
            query = exchange.getRequestURI().getRawQuery();
            var body = PERSON_JSON.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, body.length);
            try (var output = exchange.getResponseBody()) {
                output.write(body);
            }
        });
        server.start();
        registry.add("person.client.url", () -> "http://127.0.0.1:" + server.getAddress().getPort() + "/person");
    }

    @AfterAll
    static void stopServer() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Autowired
    private PersonClient client;

    @DisplayName("Should encode PersonStatus as getValue and read that code back from JSON")
    @Test
    void encodesMappedValueOnTheQueryString() {
        var people = client.findAll(new PersonSearchRequest(null, null, null, PersonStatus.ACTIVE));

        assertTrue(query.contains("status=A"));
        assertEquals(1, people.size());
        var person = people.iterator().next();
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000001"), person.id());
        assertEquals("Ada", person.name());
        assertEquals(LocalDate.of(1990, 1, 1), person.birthdate());
        assertEquals(PersonStatus.ACTIVE, person.status());
    }

}
