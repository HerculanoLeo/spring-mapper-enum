package com.herculanoleo.spring.me.sample.models.dto;

import java.time.LocalDate;

public record PersonUpdateRequest(
        String name,
        LocalDate birthdate
) {
}
