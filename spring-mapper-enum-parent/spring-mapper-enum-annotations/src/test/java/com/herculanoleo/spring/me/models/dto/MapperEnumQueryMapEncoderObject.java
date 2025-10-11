package com.herculanoleo.spring.me.models.dto;

import com.herculanoleo.spring.me.models.enums.MapperEnumMock;
import feign.Param;

public record MapperEnumQueryMapEncoderObject(
        String value1,
        MapperEnumMock value2,
        @Param("param3")
        MapperEnumMock value3
) {
}
