package com.herculanoleo.spring.me.models.annotation;

import com.herculanoleo.spring.me.configuration.ResourceLoader;
import com.herculanoleo.spring.me.configuration.StartConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({StartConfiguration.class, ResourceLoader.class})
public @interface EnableMapperEnum {

    String[] value() default {};

    String[] basePackages() default {};

}
