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

If you're using other libraries that generate classes at build time, such as `hibernate-processor` or `lombok`, add an annotationProcessor configuration to the build -> plugins -> plugin section of your `pom.xml`. See the [pom.xml](samples/sample-jpa/pom.xml)
in the [sample-jpa](samples/sample-jpa) module for an example.

When writing integration tests with `TestRestTemplate` on Spring Boot 4, add the test dependencies `spring-boot-starter-webmvc-test` and `spring-boot-starter-restclient`, and annotate the test class with `@AutoConfigureTestRestTemplate`. The [sample-jpa](samples/sample-jpa) module demonstrates this setup.

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
import com.herculanoleo.spring.me.models.enums.MapperEnum;

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
*   Scan for all classes implementing `MapperEnum`.
*   Register Spring `Formatter`s for each found `MapperEnum` type, enabling automatic conversion in Spring MVC.
*   Register a Jackson module (`mapperEnumModule`) with custom serializers and deserializers for all found `MapperEnum` types (auto-configured by Spring Boot).

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

1.  Add `@EnableMapperEnum` to scan enums and register MVC/JSON converters.
2.  Add `@EnableFeignMapperEnum` to register the custom `MapperEnumQueryMapEncoder` and Feign formatters.

`@EnableFeignMapperEnum` requires `@EnableMapperEnum` on the same application (it reuses the shared `MapperResourceLoader` bean).

### 4. JPA Persistence

To persist `MapperEnum` instances as their string values in a database using JPA:

1.  Ensure `spring-data-jpa` is a dependency in your project.
2.  Annotate your `MapperEnum` implementation with `@MapperEnumDBConverter`.
3.  The annotation processor will scan all interfaces that feature the specified annotation. It will then generate a `AttributeConverter` class for each Enum encountered, as part of the project's build process.

## Configuration Options
*   `@EnableMapperEnum(basePackages = {"com.example.enums", "com.another.package.enums"})`: Specifies an array of base packages to scan for MapperEnum implementations.
*   `@EnableMapperEnum(value = {"com.example.enums"})`: Alias for basePackages.  

If basePackages (or value) is not specified, the scanning starts from the package of the class annotated with `@EnableMapperEnum`.