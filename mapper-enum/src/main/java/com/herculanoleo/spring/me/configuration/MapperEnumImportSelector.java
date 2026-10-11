package com.herculanoleo.spring.me.configuration;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;

import java.util.ArrayList;

/**
 * Imports MVC, Jackson, Feign, and WebFlux configuration only when those types are on the classpath.
 */
public class MapperEnumImportSelector implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        var imports = new ArrayList<String>();
        imports.add(MapperResourceLoader.class.getName());
        if (ClassUtils.isPresent("tools.jackson.databind.JacksonModule", getClass().getClassLoader())) {
            imports.add(StartConfiguration.class.getName());
        }
        if (ClassUtils.isPresent("org.springframework.web.servlet.config.annotation.WebMvcConfigurer", getClass().getClassLoader())) {
            imports.add(MapperEnumWebMvcConfiguration.class.getName());
        }
        if (ClassUtils.isPresent("org.springframework.cloud.openfeign.FeignFormatterRegistrar", getClass().getClassLoader())) {
            imports.add(FeignStartConfiguration.class.getName());
        }
        if (ClassUtils.isPresent("org.springframework.web.reactive.config.WebFluxConfigurer", getClass().getClassLoader())) {
            imports.add(MapperEnumWebFluxConfiguration.class.getName());
        }
        if (ClassUtils.isPresent("org.springdoc.core.customizers.PropertyCustomizer", getClass().getClassLoader())) {
            imports.add(MapperEnumOpenApiCustomizer.class.getName());
        }
        return imports.toArray(String[]::new);
    }

}
