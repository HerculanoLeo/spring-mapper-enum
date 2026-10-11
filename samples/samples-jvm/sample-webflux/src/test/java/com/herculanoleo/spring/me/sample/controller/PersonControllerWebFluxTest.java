package com.herculanoleo.spring.me.sample.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.concurrent.atomic.AtomicReference;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PersonControllerWebFluxTest {

    @LocalServerPort
    private int port;

    @DisplayName("Should filter, persist, and reject MapperEnum values over WebFlux")
    @Test
    void convertsQueryBodyAndColumn() {
        var client = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();

        client.post().uri("/person")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"Ada\",\"birthdate\":\"1990-01-01\",\"status\":\"A\"}")
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.status").isEqualTo("A");

        var inactiveId = new AtomicReference<String>();
        client.post().uri("/person")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"Bea\",\"birthdate\":\"1991-02-02\",\"status\":\"I\"}")
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.status").isEqualTo("I")
                .jsonPath("$.id").value(inactiveId::set);

        client.get().uri("/person?status=A")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].status").isEqualTo("A")
                .jsonPath("$.length()").isEqualTo(1);

        client.get().uri("/person?status=")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(2);

        client.get().uri("/person?status=ACTIVE")
                .exchange()
                .expectStatus().isBadRequest();

        client.post().uri("/person")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"Invalid\",\"birthdate\":\"1992-03-03\",\"status\":1}")
                .exchange()
                .expectStatus().isBadRequest();

        client.put().uri("/person/" + inactiveId.get())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"Bea\",\"birthdate\":\"1991-02-02\",\"status\":\"A\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("A");
    }

}
