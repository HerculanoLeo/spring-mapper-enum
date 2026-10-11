# Spring Mapper Enum

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

A Spring Boot library that simplifies handling of enums with custom string representations across various application layers, including web requests (Spring MVC), JSON serialization/deserialization (Jackson), Feign clients, and JPA persistence.

## Features

*   **Automatic Enum-String Conversion:** Seamlessly convert enums implementing the `MapperEnum` interface to/from their custom string values in Spring MVC (request parameters, path variables).
*   **Jackson Integration:** Automatic JSON serialization to string values and deserialization from string values for `MapperEnum` instances.
*   **Feign Client Support:** Includes a custom Feign `QueryMapEncoder` for correct `MapperEnum` encoding in query parameters when using `@SpringQueryMap`.
*   **JPA Persistence:** Annotation-based generation of JPA `AttributeConverter`s (`@Converter(autoApply = true)`) for persisting `MapperEnum`s as their string values in the database.
*   **WebFlux:** A reactive application registers the same formatters through `WebFluxConfigurer`. An MVC application does not receive a WebFlux dependency from this library.
*   **OpenAPI:** When springdoc is on the classpath, schemas list the `getValue()` codes.
*   **Centralized Enum Contract:** The `MapperEnum` interface provides a standard way to define custom string values, default/generic enum instances, and custom error messages. Use `MapperEnum.fromValue(...)` for resolution by `getValue()` (fallback or `IllegalArgumentException`), or `MapperEnum.findByValue(...)` for an `Optional` lookup.
*   **Easy Setup:** Auto-configuration enables Spring MVC, Jackson, and, when those stacks are on the classpath, WebFlux and Feign. `@EnableMapperEnum` remains available when auto-configuration is excluded.
*   **Compile-Time Registration:** Enums are registered at build time via `@MappedEnum` (no runtime classpath scanning), which keeps startup predictable and works well with GraalVM native images.
*   **Reduced Boilerplate:** Minimizes the need for manual converter and serializer/deserializer implementations.

## Why use this library?

*   **Consistency:** Enforces a standardized way of handling enums with specific string representations throughout your application.
*   **Developer Productivity:** Reduces boilerplate code, allowing developers to focus on business logic.
*   **Improved Readability:** Abstracts conversion logic, leading to cleaner controller, service, and entity code.
*   **Type Safety with Flexibility:** Combines the type safety of Java enums with the flexibility of custom string mappings.

## Requirements

*   Java 17+
*   Spring Boot 4.0.x
*   Spring MVC
*   Jackson 3 (`tools.jackson`)
*   **Optional:**
    *   Spring Cloud OpenFeign (for Feign client integration)
    *   Spring Data JPA (for JPA `AttributeConverter` generation)
    *   Spring WebFlux (query and path conversion on a reactive application; JSON already uses the shared Jackson module)
    *   springdoc OpenAPI (schemas list `getValue()` codes)

## Version compatibility

This repository maintains two release lines:

| Line | Branch | Spring Boot | Spring Native |
|------|--------|-------------|---------------|
| **v2** | `v2.x.x` (default) | 4.0.x | Supported (compile-time registration, no runtime classpath scanning) |
| **v1** | [`v1.x.x`](https://github.com/HerculanoLeo/spring-mapper-enum/tree/v1.x.x) | 3.2.x – 3.5.x | **Not supported** |

**Spring Boot 3.2 through 3.5:** use the **v1** line — check out the [`v1.x.x`](https://github.com/HerculanoLeo/spring-mapper-enum/tree/v1.x.x) branch and depend on a `1.x.y` release from that line. It targets Jackson 2 and the Spring Boot 3 ecosystem.

**Spring Native / GraalVM on v1:** the v1 line is **not directly compatible** with Spring Native. Enum discovery relies on **reflection** at runtime, which does not fit GraalVM native image constraints without substantial manual reachability configuration. If you need native images, migrate to **v2** on Spring Boot 4.

## Installation

Add the dependency to your project.

**Maven:**

```xml
<dependency>
    <groupId>com.herculanoleo</groupId>
    <artifactId>spring-mapper-enum</artifactId>
    <version>2.0.0</version>
</dependency>
<dependency>
    <groupId>com.herculanoleo</groupId>
    <artifactId>spring-mapper-enum-processor</artifactId>
    <version>2.0.0</version>
    <scope>provided</scope>
    <optional>true</optional>
</dependency>
```

**Gradle:**
```groovy
implementation 'com.herculanoleo:spring-mapper-enum:2.0.0'
annotationProcessor 'com.herculanoleo:spring-mapper-enum:2.0.0'
annotationProcessor 'com.herculanoleo:spring-mapper-enum-processor:2.0.0'
```
*(The annotation processor path does not see the compile classpath, so both artifacts must be listed there. `provided` and `optional` keep the processor out of the packaged application.)*

To download the dependency using GitHub packages, follow these steps: [Working with the Apache Maven registry](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry)

## Observations

Add `spring-mapper-enum-processor` and `spring-mapper-enum` to `annotationProcessorPaths` in `maven-compiler-plugin` so the library can generate JSON serializers/deserializers, `MapperEnumTypeContributor` implementations, and (when applicable) JPA converters at compile time. The processor path is isolated, so the runtime artifact must be listed next to the processor. If you use other code generators (`hibernate-processor`, `lombok`, etc.), list them together in the same `annotationProcessorPaths` block. See [samples/samples-jvm/sample-jpa/pom.xml](samples/samples-jvm/sample-jpa/pom.xml) for a full example.

When writing integration tests with `TestRestTemplate` on Spring Boot 4, add the test dependencies `spring-boot-starter-webmvc-test` and `spring-boot-starter-restclient`, and annotate the test class with `@AutoConfigureTestRestTemplate`. The [samples-jvm/sample-jpa](samples/samples-jvm/sample-jpa) module demonstrates this setup.

Sample layout: [samples-jvm](samples/samples-jvm/) (JPA + Feign on the JVM) and [samples-native](samples/samples-native/) (same apps for GraalVM native image).

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.14.1</version>
    <configuration>
        <generatedSourcesDirectory>${project.build.directory}/generated-sources/</generatedSourcesDirectory>
        <annotationProcessorPaths>
            <path>
                <groupId>org.hibernate.orm</groupId>
                <artifactId>hibernate-processor</artifactId>
                <version>${hibernate.version}</version>
            </path>
            <path>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
                <version>${lombok.version}</version>
            </path>
            <path>
                <groupId>com.herculanoleo</groupId>
                <artifactId>spring-mapper-enum-processor</artifactId>
                <version>${project.version}</version>
            </path>
            <path>
                <groupId>com.herculanoleo</groupId>
                <artifactId>spring-mapper-enum</artifactId>
                <version>${project.version}</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

## Core Concept: The `MapperEnum` Interface

Any enum that you want this library to manage must implement the `com.herculanoleo.spring.me.models.enums.MapperEnum` interface.

**Example `MapperEnum` Implementation:**

```java 
import com.herculanoleo.spring.me.models.annotation.MappedEnum;
import com.herculanoleo.spring.me.models.enums.MapperEnum;

@MappedEnum
public enum TaskStatus implements MapperEnum {
    TODO("T"),
    DOING("D"),
    COMPLETED("C"),
    ;
    
    private final String value;
    
    public TaskStatus(String value) {
        this.value = value;
    }
    
    @Override
    public String getValue() {
        return value;
    }
    
}
```

## Enabling the Library

Auto-configuration loads the library when `spring-mapper-enum` is on the classpath. `@EnableMapperEnum` imports the same configuration for applications that exclude auto-configuration.

This will:
*   Load all `MapperEnum` types registered at compile time (via `META-INF/services` and generated `MapperEnumTypeContributor` classes), using the Spring bean class loader.
*   Register Spring `Formatter`s for each registered type on servlet MVC. A reactive application registers the same formatters through `WebFluxConfigurer`.
*   Register a Jackson module (`mapperEnumModule`) with type-specific serializers and deserializers.

Every enum must be annotated with `@MappedEnum` and implement `MapperEnum`. The annotation processor must run during your application build. Incoming values match `getValue()` only. A blank string is treated as no filter and becomes `null` in JSON, query parameters, and JPA columns. An unknown value throws `IllegalArgumentException`, which MVC wraps as a conversion failure and Jackson reports as `InvalidFormatException` (HTTP 400).

`@MappedEnum(fallback = "CONSTANT_NAME")` maps unknown values to that constant. The processor warns when a fallback exists. Use a fallback only when an unknown value can safely become that constant. Authorization enums should omit it so unknown input fails.

## Usage Scenarios

### 1. Spring MVC and WebFlux

Formatters convert request parameters and path variables. MVC uses `mvcConversionService` when the application is a servlet app. WebFlux uses `webFluxConversionService` only when `spring.main.web-application-type=reactive` (or when WebFlux is the only web stack). An MVC application does not receive a WebFlux dependency from this library.

### 2. JSON Serialization/Deserialization (Jackson)

Jackson serializes `MapperEnum` instances to their `getValue()` string and deserializes them with `MapperEnum.fromValue()`. A blank string becomes `null`. A number or object token is rejected.

**Example DTO:**

```java
public record TaskDto(String name, TaskStatus status) {}
```

**Serialization:**
An instance `new TaskDto("My Task", TaskStatus.COMPLETED)` will be serialized to:

```json 
{
  "name": "My Task",
  "status": "C"
}
```

**Deserialization:**
The above JSON will be deserialized back to a `TaskDto` with `status` as `TaskStatus.COMPLETED`.

### 3. Feign Client Integration

When `spring-cloud-starter-openfeign` is on the classpath, auto-configuration registers `MapperEnumQueryMapEncoder` unless the application already defined a `QueryMapEncoder` bean. `@EnableFeignMapperEnum` imports that same configuration. The encoder reads record accessors and JavaBean getters, writes `getValue()` for mapper enums, and skips `static` and `transient` properties.

### 4. JPA Persistence

To persist `MapperEnum` instances as their string values in a database using JPA:

1.  Ensure `spring-data-jpa` is a dependency in your project.
2.  Annotate your enum with `@MappedEnum`.
3.  The annotation processor generates an `AttributeConverter` when `jakarta.persistence` is visible at compile time. `@MappedEnum(persist = false)` skips that converter. `@MappedEnum(autoApply = false)` keeps the converter from applying to every field of that type. Blank column values are read back as `null`.

## GraalVM / Spring Native

> **v1.x.x (Spring Boot 3.2–3.5):** not supported — see [Version compatibility](#version-compatibility). The v1 line uses reflection for enum discovery and is not suitable for Spring Native out of the box.

On the **v2** line, enum types are discovered through generated `MapperEnumTypeContributor` classes and `META-INF/services` entries (not runtime classpath scanning), so the library is compatible with native image builds as long as:

*   Every enum is annotated with `@MappedEnum`.
*   The annotation processor runs in the application build.
*   Native reachability metadata includes your generated contributor and JSON classes (Spring Boot 4 native support generally picks up `META-INF/services` automatically).

See [samples/samples-native](samples/samples-native/) for JPA and Feign apps mirrored from [samples-jvm](samples/samples-jvm/) and instructions to build native executables (`mvn -Pnative native:compile`).