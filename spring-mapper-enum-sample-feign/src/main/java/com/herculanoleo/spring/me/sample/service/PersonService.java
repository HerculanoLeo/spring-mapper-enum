package com.herculanoleo.spring.me.sample.service;

import com.herculanoleo.spring.me.sample.models.dto.PersonRegisterRequest;
import com.herculanoleo.spring.me.sample.models.dto.PersonResponse;
import com.herculanoleo.spring.me.sample.models.dto.PersonSearchRequest;
import com.herculanoleo.spring.me.sample.models.dto.PersonUpdateRequest;
import com.herculanoleo.spring.me.sample.openfeing.PersonClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonClient client;

    public Collection<PersonResponse> findAll(final PersonSearchRequest requestEntity) {
        return client.findAll(requestEntity);
    }

    public PersonResponse findById(final UUID id) {
        return client.findById(id);
    }

    public PersonResponse register(final PersonRegisterRequest requestEntity) {
        return client.register(requestEntity);
    }

    public PersonResponse update(final UUID id, final PersonUpdateRequest requestEntity) {
        return client.update(id, requestEntity);
    }

    public void active(final UUID id) {
        client.active(id);
    }

    public void inactive(final UUID id) {
        client.inactive(id);
    }

}
