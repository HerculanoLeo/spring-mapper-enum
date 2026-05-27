/**
 * Annotations to enable the library, register enums at compile time, and opt into
 * JPA or field-level formatting.
 *
 * <p>Typical setup:
 * <ol>
 *   <li>Implement {@link com.herculanoleo.spring.me.models.enums.MapperEnum} on your enum.</li>
 *   <li>Annotate the enum with {@link MapperEnumType} (and {@link MapperEnumDBConverter} for JPA).</li>
 *   <li>Add {@code spring-mapper-enum} to {@code annotationProcessorPaths} in your build.</li>
 *   <li>Annotate your application with {@link EnableMapperEnum}.</li>
 * </ol>
 */
package com.herculanoleo.spring.me.models.annotation;
