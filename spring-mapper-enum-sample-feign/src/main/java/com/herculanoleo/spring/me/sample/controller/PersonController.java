package com.herculanoleo.spring.me.sample.controller;

import com.herculanoleo.spring.me.sample.models.dto.PersonRegisterRequest;
import com.herculanoleo.spring.me.sample.models.dto.PersonResponse;
import com.herculanoleo.spring.me.sample.models.dto.PersonSearchRequest;
import com.herculanoleo.spring.me.sample.models.dto.PersonUpdateRequest;
import com.herculanoleo.spring.me.sample.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.UUID;

@RestController
@RequestMapping("person")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService service;

    @GetMapping
    public ResponseEntity<Collection<PersonResponse>> findAll(PersonSearchRequest requestEntity) {
        return ResponseEntity.ok(service.findAll(requestEntity));
    }

    @GetMapping("{id}")
    public ResponseEntity<PersonResponse> findById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<PersonResponse> register(@RequestBody PersonRegisterRequest requestEntity) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.register(requestEntity));
    }

    @PutMapping("{id}")
    public ResponseEntity<PersonResponse> update(@PathVariable("id") UUID id, @RequestBody PersonUpdateRequest requestEntity) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(service.update(id, requestEntity));
    }

    @PutMapping("{id}/active")
    public ResponseEntity<Void> active(@PathVariable("id") UUID id) {
        service.active(id);
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping("{id}/inactive")
    public ResponseEntity<Void> inactive(@PathVariable("id") UUID id) {
        service.inactive(id);
        return ResponseEntity.accepted().build();
    }

}
