package com.herculanoleo.spring.me.sample.models.dto;

import com.herculanoleo.spring.me.sample.models.enums.PersonStatus;

import java.time.LocalDate;

public record PersonSearchRequest(
        String name,
        LocalDate birthdateFrom,
        LocalDate birthdateTo,
        PersonStatus status
) {
}
