package com.herculanoleo.spring.me.sample.controller;

import com.herculanoleo.spring.me.sample.models.dto.PersonWriteRequest;
import com.herculanoleo.spring.me.sample.models.enums.PersonStatus;
import com.herculanoleo.spring.me.sample.persistence.entity.Person;
import com.herculanoleo.spring.me.sample.service.PersonService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("person")
public class PersonController {

    private final PersonService service;

    public PersonController(PersonService service) {
        this.service = service;
    }

    @GetMapping
    public Flux<Person> findAll(@RequestParam(required = false) PersonStatus status) {
        return service.findAll(status);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Person> register(@RequestBody PersonWriteRequest request) {
        return service.register(request);
    }

    @PutMapping("{id}")
    public Mono<Person> update(@PathVariable UUID id, @RequestBody PersonWriteRequest request) {
        return service.update(id, request);
    }

}
