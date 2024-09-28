package com.herculanoleo.spring.me.sample.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.herculanoleo.spring.me.sample.models.dto.PersonRegisterRequest;
import com.herculanoleo.spring.me.sample.models.dto.PersonUpdateRequest;
import com.herculanoleo.spring.me.sample.persistence.entity.Person;
import com.herculanoleo.spring.me.sample.models.enums.PersonStatus;
import com.herculanoleo.spring.me.sample.persistence.repository.PersonRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class PersonControllerE2ETest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ObjectMapper objectMapper;

    private Person johnDoeEntity;

    private Person zoeDoeEntity;

    @BeforeEach
    public void populateDB() {
        johnDoeEntity = personRepository.save(Person.builder()
                .name("John Doe")
                .birthdate(LocalDate.of(2000, 1, 1))
                .status(PersonStatus.ACTIVE)
                .build());
        zoeDoeEntity = personRepository.save(Person.builder()
                .name("Zoe Doe")
                .birthdate(LocalDate.of(2001, 1, 1))
                .status(PersonStatus.INACTIVE)
                .build());
    }

    @AfterEach
    public void cleanDB() {
        personRepository.deleteAll();
        johnDoeEntity = null;
        zoeDoeEntity = null;
    }

    @DisplayName("Should execute a get to /person and return two instances of person")
    @Test
    public void findAll_two_person_test() {
        ResponseEntity<Collection<Person>> responseEntity = restTemplate.exchange(
                "/person",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        Collection<Person> body = responseEntity.getBody();

        assertNotNull(body);
        assertTrue(body.containsAll(List.of(johnDoeEntity, zoeDoeEntity)));
    }

    @DisplayName("Should execute a get to /person with status active filter and return one instance of person")
    @Test
    public void findAll_active_person_test() {
        var expectedResult = johnDoeEntity;

        var rootUri = restTemplate.getRootUri();

        var uri = UriComponentsBuilder.fromUriString(rootUri)
                .path("/person")
                .queryParam("status", PersonStatus.ACTIVE.getValue()).toUriString();

        var responseEntity = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Collection<Person>>() {
                }
        );

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        var body = responseEntity.getBody();

        assertNotNull(body);
        assertEquals(1, body.size());
        assertTrue(body.contains(expectedResult));
    }

    @DisplayName("Should execute a get to /person/{id} and validate json response")
    @Test
    public void findById_validate_json_response_test() throws JsonProcessingException {
        var responseEntity = restTemplate.getForEntity("/person/" + johnDoeEntity.getId(), String.class);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        var body = responseEntity.getBody();

        assertNotNull(body);

        var jsonNode = objectMapper.readTree(body);
        assertEquals(johnDoeEntity.getId().toString(), jsonNode.get("id").asText());
        assertEquals("John Doe", jsonNode.get("name").asText());
        assertEquals("2000-01-01", jsonNode.get("birthdate").asText());
        assertEquals("A", jsonNode.get("status").asText());
    }

    @DisplayName("Should execute a post to /person and register to database")
    @Test
    public void register_save_person_test() {
        var requestEntity = new PersonRegisterRequest("James Doe", LocalDate.of(2001, 1, 1));

        var responseEntity = restTemplate.postForEntity("/person", requestEntity, Person.class);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

        var body = responseEntity.getBody();

        assertNotNull(body);
        assertEquals(requestEntity.name(), body.getName());
        assertEquals(requestEntity.birthdate(), body.getBirthdate());
        assertEquals(PersonStatus.ACTIVE, body.getStatus());

        Query nativeQuery = entityManager.createNativeQuery("SELECT p.id, p.name, p.birthdate, p.status FROM person p WHERE p.id = ?");
        nativeQuery = nativeQuery.setParameter(1, body.getId());

        Object[] object = (Object[]) nativeQuery.getSingleResult();

        assertEquals("James Doe", object[1]);
        assertEquals(
                LocalDate.of(2001, 1, 1),
                Instant
                        .ofEpochMilli(((Date) object[2]).getTime())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
        );
        assertEquals(PersonStatus.ACTIVE.getValue(), object[3]);
    }

    @DisplayName("Should execute a put to /person/{id} and update data from database")
    @Test
    public void update_person_test() {
        this.restTemplate.put("/person/" + johnDoeEntity.getId(), new PersonUpdateRequest(
                "John Doe - Edit",
                LocalDate.of(2001, 1, 1)
        ));

        Query nativeQuery = entityManager.createNativeQuery("SELECT p.id, p.name, p.birthdate, p.status FROM person p WHERE p.id = ?");
        nativeQuery = nativeQuery.setParameter(1, johnDoeEntity.getId());

        Object[] object = (Object[]) nativeQuery.getSingleResult();

        assertEquals("John Doe - Edit", object[1]);
        assertEquals(
                LocalDate.of(2001, 1, 1),
                Instant
                        .ofEpochMilli(((Date) object[2]).getTime())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
        );
        assertEquals(PersonStatus.ACTIVE.getValue(), object[3]);
    }

    @DisplayName("Should execute put to /person/{id}/active and update status from database")
    @Test
    public void active_person_test() {
        this.restTemplate.put("/person/" + zoeDoeEntity.getId() + "/active", null);

        Query nativeQuery = entityManager.createNativeQuery("SELECT p.id, p.name, p.birthdate, p.status FROM person p WHERE p.id = ?");
        nativeQuery = nativeQuery.setParameter(1, zoeDoeEntity.getId());

        Object[] object = (Object[]) nativeQuery.getSingleResult();

        assertEquals("Zoe Doe", object[1]);
        assertEquals(
                LocalDate.of(2001, 1, 1),
                Instant
                        .ofEpochMilli(((Date) object[2]).getTime())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
        );
        assertEquals(PersonStatus.ACTIVE.getValue(), object[3]);
    }

    @DisplayName("Should execute put to /person/{id}/active and throw error when person already active")
    @Test
    public void active_person_error_test() {
        var responseEntity = this.restTemplate.exchange(
                "/person/" + johnDoeEntity.getId() + "/active",
                HttpMethod.PUT,
                null,
                Void.class
        );
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @DisplayName("Should execute delete to /person/{id}/inactive and update status from database")
    @Test
    public void inactive_person_test() {
        this.restTemplate.delete("/person/" + johnDoeEntity.getId() + "/inactive");

        Query nativeQuery = entityManager.createNativeQuery("SELECT p.id, p.name, p.birthdate, p.status FROM person p WHERE p.id = ?");
        nativeQuery = nativeQuery.setParameter(1, johnDoeEntity.getId());

        Object[] object = (Object[]) nativeQuery.getSingleResult();

        assertEquals("John Doe", object[1]);
        assertEquals(
                LocalDate.of(2000, 1, 1),
                Instant
                        .ofEpochMilli(((Date) object[2]).getTime())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
        );
        assertEquals(PersonStatus.INACTIVE.getValue(), object[3]);
    }

    @DisplayName("Should execute delete to /person/{id}/active and throw error when person already inactive")
    @Test
    public void inactive_person_error_test() {
        var responseEntity = this.restTemplate.exchange(
                "/person/" + zoeDoeEntity.getId() + "/inactive",
                HttpMethod.DELETE,
                null,
                Void.class
        );
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

}
