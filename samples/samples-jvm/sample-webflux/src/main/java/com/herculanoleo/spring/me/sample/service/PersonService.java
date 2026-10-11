package com.herculanoleo.spring.me.sample.service;

import com.herculanoleo.spring.me.sample.models.dto.PersonWriteRequest;
import com.herculanoleo.spring.me.sample.models.enums.PersonStatus;
import com.herculanoleo.spring.me.sample.persistence.entity.Person;
import com.herculanoleo.spring.me.sample.persistence.repository.PersonRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

@Service
public class PersonService {

    private final PersonRepository repository;

    public PersonService(PersonRepository repository) {
        this.repository = repository;
    }

    public Flux<Person> findAll(PersonStatus status) {
        return Mono.fromCallable(() -> status == null ? repository.findAll() : repository.findByStatus(status))
                .flatMapMany(Flux::fromIterable)
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Person> register(PersonWriteRequest request) {
        return Mono.fromCallable(() -> repository.save(Person.builder()
                        .name(request.name())
                        .birthdate(request.birthdate())
                        .status(request.status())
                        .build()))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Person> update(UUID id, PersonWriteRequest request) {
        return Mono.fromCallable(() -> {
                    var entity = repository.findById(id).orElseThrow();
                    entity.setName(request.name());
                    entity.setBirthdate(request.birthdate());
                    entity.setStatus(request.status());
                    return repository.save(entity);
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

}
