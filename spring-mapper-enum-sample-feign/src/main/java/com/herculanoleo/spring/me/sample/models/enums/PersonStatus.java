package com.herculanoleo.spring.me.sample.models.enums;

import com.herculanoleo.spring.me.models.annotation.MapperEnumDBConverter;
import com.herculanoleo.spring.me.models.enums.MapperEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@MapperEnumDBConverter
public enum PersonStatus implements MapperEnum {

    ACTIVE("A"), INACTIVE("I");

    private final String value;

}
