package com.herculanoleo.spring.me.sample.models.dto;

import com.herculanoleo.spring.me.sample.models.enums.PersonStatus;

import java.time.LocalDate;

public record PersonWriteRequest(
        String name,
        LocalDate birthdate,
        PersonStatus status
) {
}
