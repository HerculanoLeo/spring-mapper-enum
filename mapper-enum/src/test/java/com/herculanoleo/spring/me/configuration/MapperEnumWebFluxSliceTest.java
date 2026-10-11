package com.herculanoleo.spring.me.configuration;

import com.herculanoleo.spring.me.models.enums.MapperEnumMock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest(
        classes = MapperEnumWebFluxSliceTest.App.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.main.web-application-type=reactive"
)
class MapperEnumWebFluxSliceTest {

    @LocalServerPort
    private int port;

    @DisplayName("Should convert MapperEnum on a reactive query and JSON body")
    @Test
    void convertsQueryAndBody() {
        var client = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();

        client.get().uri("/status?status=A")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("A");

        client.get().uri("/status?status=")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("empty");

        client.post().uri("/status")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"status\":\"I\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("I");

        client.post().uri("/status")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"status\":1}")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    static class App {

        @Bean
        StatusController statusController() {
            return new StatusController();
        }
    }

    @RestController
    static class StatusController {

        @GetMapping("/status")
        String byQuery(@RequestParam(required = false) MapperEnumMock status) {
            return status == null ? "empty" : status.getValue();
        }

        @PostMapping(value = "/status", consumes = MediaType.APPLICATION_JSON_VALUE)
        String byBody(@RequestBody StatusBody body) {
            return body.status() == null ? "empty" : body.status().getValue();
        }
    }

    record StatusBody(MapperEnumMock status) {
    }
}
