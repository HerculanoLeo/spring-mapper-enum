package com.herculanoleo.spring.me.sample.persistence.specification;

import com.herculanoleo.spring.me.sample.persistence.entity.Person;
import com.herculanoleo.spring.me.sample.persistence.entity.Person_;
import com.herculanoleo.spring.me.sample.models.enums.PersonStatus;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Objects;

@UtilityClass
public class PersonSpecification {

    public static Specification<Person> nameLike(final String name) {
        return (root, query, criteriaBuilder) -> {
            if (StringUtils.isNotBlank(name)) {
                return criteriaBuilder.like(criteriaBuilder.upper(root.get(Person_.name)), "%" + name.toUpperCase() + "%");
            }
            return null;
        };
    }

    public static Specification<Person> birthdateGreaterThanOrEqualTo(final LocalDate date) {
        return (root, query, criteriaBuilder) -> {
            if (Objects.nonNull(date)) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get(Person_.birthdate), date);
            }
            return null;
        };
    }

    public static Specification<Person> birthdateLessThanOrEqualTo(final LocalDate date) {
        return (root, query, criteriaBuilder) -> {
            if (Objects.nonNull(date)) {
                return criteriaBuilder.lessThanOrEqualTo(root.get(Person_.birthdate), date);
            }
            return null;
        };
    }

    public static Specification<Person> status(final PersonStatus status) {
        return (root, query, criteriaBuilder) -> {
            if (Objects.nonNull(status)) {
                return criteriaBuilder.equal(root.get(Person_.status), status);
            }
            return null;
        };
    }

}
