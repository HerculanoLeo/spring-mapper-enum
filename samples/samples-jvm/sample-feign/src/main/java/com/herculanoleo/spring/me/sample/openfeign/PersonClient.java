package com.herculanoleo.spring.me.sample.openfeign;

import com.herculanoleo.spring.me.sample.models.dto.PersonRegisterRequest;
import com.herculanoleo.spring.me.sample.models.dto.PersonResponse;
import com.herculanoleo.spring.me.sample.models.dto.PersonSearchRequest;
import com.herculanoleo.spring.me.sample.models.dto.PersonUpdateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.UUID;

@FeignClient(
        name = "person-client",
        url = "${person.client.url}"
)
public interface PersonClient {

    @GetMapping
    Collection<PersonResponse> findAll(@SpringQueryMap PersonSearchRequest requestEntity);

    @GetMapping("{id}")
    PersonResponse findById(@PathVariable UUID id);

    @PostMapping
    PersonResponse register(@RequestBody PersonRegisterRequest requestEntity);

    @PutMapping("{id}")
    PersonResponse update(@PathVariable UUID id, @RequestBody PersonUpdateRequest requestEntity);

    @PutMapping("{id}/active")
    Void active(@PathVariable UUID id);

    @DeleteMapping("{id}/inactive")
    Void inactive(@PathVariable UUID id);

}
