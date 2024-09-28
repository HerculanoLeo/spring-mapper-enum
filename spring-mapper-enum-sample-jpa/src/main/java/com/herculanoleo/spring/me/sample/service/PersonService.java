package com.herculanoleo.spring.me.sample.service;

import com.herculanoleo.spring.me.sample.models.dto.PersonRegisterRequest;
import com.herculanoleo.spring.me.sample.models.dto.PersonSearchRequest;
import com.herculanoleo.spring.me.sample.models.dto.PersonUpdateRequest;
import com.herculanoleo.spring.me.sample.persistence.entity.Person;
import com.herculanoleo.spring.me.sample.models.enums.PersonStatus;
import com.herculanoleo.spring.me.sample.persistence.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

import static com.herculanoleo.spring.me.sample.persistence.specification.PersonSpecification.*;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository repository;

    public Collection<Person> findAll(final PersonSearchRequest requestEntity) {
        return this.repository.findAll(Specification
                .where(nameLike(requestEntity.name()))
                .and(birthdateGreaterThanOrEqualTo(requestEntity.birthdateFrom()))
                .and(birthdateLessThanOrEqualTo(requestEntity.birthdateTo()))
                .and(status(requestEntity.status()))
        );
    }

    public Person findById(final UUID id) {
        return this.repository.findById(id).orElseThrow();
    }

    @Transactional(rollbackFor = Throwable.class)
    public Person register(final PersonRegisterRequest requestEntity) {
        return repository.save(Person.builder()
                .name(requestEntity.name())
                .birthdate(requestEntity.birthdate())
                .status(PersonStatus.ACTIVE)
                .build());
    }

    @Transactional(rollbackFor = Throwable.class)
    public Person update(final UUID id, final PersonUpdateRequest requestEntity) {
        var entity = this.findById(id);

        entity.setName(requestEntity.name());
        entity.setBirthdate(requestEntity.birthdate());

        return this.repository.save(entity);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void active(final UUID id) {
        var entity = this.findById(id);

        if (Objects.equals(PersonStatus.ACTIVE, entity.getStatus())) {
            throw new RuntimeException("cannot activate person that already active");
        }

        entity.setStatus(PersonStatus.ACTIVE);

        this.repository.save(entity);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void inactive(final UUID id) {
        var entity = this.findById(id);

        if (Objects.equals(PersonStatus.INACTIVE, entity.getStatus())) {
            throw new RuntimeException("cannot inactivate person that already inactive");
        }

        entity.setStatus(PersonStatus.INACTIVE);

        this.repository.save(entity);
    }

}
