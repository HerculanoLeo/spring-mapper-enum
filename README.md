# Spring Mapper Enum

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

A Spring Boot library that simplifies handling of enums with custom string representations across various application layers, including web requests (Spring MVC), JSON serialization/deserialization (Jackson), Feign clients, and JPA persistence.

## Features

*   **Automatic Enum-String Conversion:** Seamlessly convert enums implementing the `MapperEnum` interface to/from their custom string values in Spring MVC (request parameters, path variables).
*   **Jackson Integration:** Automatic JSON serialization to string values and deserialization from string values for `MapperEnum` instances.
*   **Feign Client Support:** Includes a custom Feign `QueryMapEncoder` for correct `MapperEnum` encoding in query parameters when using `@SpringQueryMap`.
*   **JPA Persistence:** Annotation-based generation of JPA `AttributeConverter`s (`@Converter(autoApply = true)`) for persisting `MapperEnum`s as their string values in the database.
*   **Centralized Enum Contract:** The `MapperEnum` interface provides a standard way to define custom string values, default/generic enum instances, and custom error messages.
*   **Easy Setup:** Quickly enable features with `@EnableMapperEnum` for core Spring/Jackson and `@EnableFeignMapperEnum` for Feign integration.
*   **Compile-Time Registration:** Enums are registered at build time via `@MapperEnumType` (no runtime classpath scanning), which keeps startup predictable and works well with GraalVM native images.
*   **Reduced Boilerplate:** Minimizes the need for manual converter and serializer/deserializer implementations.

## Why use this library?

*   **Consistency:** Enforces a standardized way of handling enums with specific string representations throughout your application.
*   **Developer Productivity:** Reduces boilerplate code, allowing developers to focus on business logic.
*   **Improved Readability:** Abstracts conversion logic, leading to cleaner controller, service, and entity code.
*   **Type Safety with Flexibility:** Combines the type safety of Java enums with the flexibility of custom string mappings.

## Requirements

*   Java 17+
*   Spring Boot 4.0.x (**v2.x.x** line — this branch)
*   Spring MVC
*   Jackson 3 (`tools.jackson`)
*   **Optional:**
    *   Spring Cloud OpenFeign (for Feign client integration)
    *   Spring Data JPA (for JPA `AttributeConverter` generation)

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
    <version>X.Y.Z</version> 
</dependency>
```

**Gradle:**
```groovy
implementation 'com.herculanoleo:spring-mapper-enum:X.Y.Z'
```
*(Note: Replace X.Y.Z with the latest library version.)*

To download the dependency using GitHub packages, follow these steps: [Working with the Apache Maven registry](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry)

## Observations

Add `spring-mapper-enum` to `annotationProcessorPaths` in `maven-compiler-plugin` so the library can generate JSON serializers/deserializers, `MapperEnumTypeContributor` implementations, and (when applicable) JPA converters at compile time. If you use other code generators (`hibernate-processor`, `lombok`, etc.), list them together in the same `annotationProcessorPaths` block. See [samples/samples-jvm/sample-jpa/pom.xml](samples/samples-jvm/sample-jpa/pom.xml) for a full example.

When writing integration tests with `TestRestTemplate` on Spring Boot 4, add the test dependencies `spring-boot-starter-webmvc-test` and `spring-boot-starter-restclient`, and annotate the test class with `@AutoConfigureTestRestTemplate`. The [samples-jvm/sample-jpa](samples/samples-jvm/sample-jpa) module demonstrates this setup.

Sample layout: [samples-jvm](samples/samples-jvm/) (JPA + Feign on the JVM) and [samples-native](samples/samples-native/) (same apps for GraalVM native image).

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.13.0</version>
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
import com.herculanoleo.spring.me.models.annotation.MapperEnumDBConverter;
import com.herculanoleo.spring.me.models.annotation.MapperEnumType;
import com.herculanoleo.spring.me.models.enums.MapperEnum;

@MapperEnumType
@MapperEnumDBConverter
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

To activate the library's features, add the `@EnableMapperEnum` annotation to one of your Spring `@Configuration` classes (often your main application class).

This will:
*   Load all `MapperEnum` types registered at compile time (via `META-INF/services` and generated `MapperEnumTypeContributor` classes).
*   Register Spring `Formatter`s for each registered type, enabling automatic conversion in Spring MVC.
*   Register a Jackson module (`mapperEnumModule`) with type-specific serializers and deserializers (auto-configured by Spring Boot).

Every enum must be annotated with `@MapperEnumType` (and `@MapperEnumDBConverter` when JPA is used). The annotation processor must run during your application build.

## Usage Scenarios

### 1. Spring MVC (Web Conversion)

Once `@EnableMapperEnum` is active, enums implementing `MapperEnum` will be automatically converted from request parameters or path variables to their corresponding enum instances, and vice-versa.

### 2. JSON Serialization/Deserialization (Jackson)

With `@EnableMapperEnum`, Jackson is automatically configured to serialize `MapperEnum` instances to their `getValue()` string and deserialize them back using `MapperEnum.fromValue()`.

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

To enable `MapperEnum` support for Feign clients, especially when using `@SpringQueryMap`:

1.  Add `@EnableMapperEnum` to register MVC/JSON converters for compile-time registered enums.
2.  Add `@EnableFeignMapperEnum` to register the custom `MapperEnumQueryMapEncoder` and Feign formatters.

`@EnableFeignMapperEnum` requires `@EnableMapperEnum` on the same application (it reuses the shared `MapperResourceLoader` bean).

### 4. JPA Persistence

To persist `MapperEnum` instances as their string values in a database using JPA:

1.  Ensure `spring-data-jpa` is a dependency in your project.
2.  Annotate your enum with `@MapperEnumType` and `@MapperEnumDBConverter`.
3.  The annotation processor generates an `AttributeConverter` (`@Converter(autoApply = true)`) for each annotated enum during the build.

## GraalVM / Spring Native

> **v1.x.x (Spring Boot 3.2–3.5):** not supported — see [Version compatibility](#version-compatibility). The v1 line uses reflection for enum discovery and is not suitable for Spring Native out of the box.

On the **v2** line, enum types are discovered through generated `MapperEnumTypeContributor` classes and `META-INF/services` entries (not runtime classpath scanning), so the library is compatible with native image builds as long as:

*   Every enum is annotated with `@MapperEnumType` (and `@MapperEnumDBConverter` when using JPA).
*   The annotation processor runs in the application build.
*   Native reachability metadata includes your generated contributor and JSON classes (Spring Boot 4 native support generally picks up `META-INF/services` automatically).

See [samples/samples-native](samples/samples-native/) for JPA and Feign apps mirrored from [samples-jvm](samples/samples-jvm/) and instructions to build native executables (`mvn -Pnative native:compile`).