package com.herculanoleo.spring.me.configuration;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Import;

/**
 * Activates Spring Mapper Enum when the library is on the classpath.
 *
 * <p>{@link com.herculanoleo.spring.me.models.annotation.EnableMapperEnum} imports the same selector.
 * Servlet, reactive, Feign, and Jackson pieces load only when their types are present.
 */
@AutoConfiguration
@AutoConfigureBefore(name = "org.springframework.cloud.openfeign.FeignAutoConfiguration")
@Import(MapperEnumImportSelector.class)
public class MapperEnumAutoConfiguration {
}
