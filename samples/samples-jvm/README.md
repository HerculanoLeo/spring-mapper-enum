# samples-jvm

JVM reference applications for spring-mapper-enum.

## Modules

- **sample-jpa** — REST API with H2, Hibernate, generated JPA converters, E2E tests with `TestRestTemplate`
- **sample-feign** — REST API plus OpenFeign client using `MapperEnum` in `@SpringQueryMap`

## Build & test

```bash
mvn -pl samples/samples-jvm -am clean test
```

## Run

```bash
mvn -pl samples/samples-jvm/sample-jpa -am spring-boot:run
mvn -pl samples/samples-jvm/sample-feign -am spring-boot:run
```

Annotation processor setup: see `sample-jpa/pom.xml` (includes `hibernate-processor` and `spring-mapper-enum`).
