package com.herculanoleo.spring.me.sample.persistence.repository;

import com.herculanoleo.spring.me.sample.models.enums.PersonStatus;
import com.herculanoleo.spring.me.sample.persistence.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PersonRepository extends JpaRepository<Person, UUID> {

    List<Person> findByStatus(PersonStatus status);

}
