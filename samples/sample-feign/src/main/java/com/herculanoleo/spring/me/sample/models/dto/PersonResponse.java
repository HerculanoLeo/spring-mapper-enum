package com.herculanoleo.spring.me.sample.models.dto;

import com.herculanoleo.spring.me.sample.models.enums.PersonStatus;

import java.time.LocalDate;
import java.util.UUID;

public record PersonResponse(
        UUID id,
        String name,
        LocalDate birthdate,
        PersonStatus status
) {
}
