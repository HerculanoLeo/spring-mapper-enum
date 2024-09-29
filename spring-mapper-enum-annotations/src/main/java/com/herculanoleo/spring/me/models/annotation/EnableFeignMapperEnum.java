package com.herculanoleo.spring.me.models.annotation;

import com.herculanoleo.spring.me.configuration.FeignStartConfiguration;
import com.herculanoleo.spring.me.configuration.MapperResourceLoader;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({FeignStartConfiguration.class, MapperResourceLoader.class})
public @interface EnableFeignMapperEnum {
}
