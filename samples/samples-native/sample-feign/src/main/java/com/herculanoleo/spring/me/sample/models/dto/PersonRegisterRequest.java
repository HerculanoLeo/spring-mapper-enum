package com.herculanoleo.spring.me.sample.models.dto;

import java.time.LocalDate;

public record PersonRegisterRequest(
        String name,
        LocalDate birthdate
) {
}
