# Samples

Example applications for **spring-mapper-enum**, organized as two mirrors:

| Tree | Purpose |
|------|---------|
| [samples-jvm](samples-jvm/) | Runnable on the JVM (development, tests, CI) |
| [samples-native](samples-native/) | Same features, packaged for GraalVM native image |

[samples-jvm](samples-jvm/) contains:

| Module | Demonstrates |
|--------|----------------|
| `sample-jpa` | Spring MVC, Jackson, and JPA (`@MappedEnum`). Query and JSON use `getValue()` (`A`, `I`). A blank `status` does not filter. `status=ACTIVE` is rejected with HTTP 400. `/v3/api-docs` lists those same codes. |
| `sample-feign` | OpenFeign `@SpringQueryMap` (`@EnableFeignMapperEnum`). `PersonStatus` is not a JPA enum here. The client writes `status=A`, and JSON `"A"` is read back as `PersonStatus.ACTIVE`. `person.client.url` points at the JPA sample. |
| `sample-webflux` | Spring WebFlux, Jackson, and JPA on an in-memory H2 database. A blank `status` does not filter. The constant name `ACTIVE` and a numeric JSON token are rejected with HTTP 400. |

[samples-native](samples-native/) mirrors `sample-jpa` and `sample-feign` for a GraalVM native image.

## Build all samples (JVM tests)

From the repository root:

```bash
mvn clean test
```

Only JVM samples:

```bash
mvn -pl samples/samples-jvm/sample-jpa,samples/samples-jvm/sample-feign,samples/samples-jvm/sample-webflux -am clean test
```

Native sample modules also run **JVM tests** in CI; native binaries are built separately (see [samples-native/README.md](samples-native/README.md)).

## Run a JVM sample

```bash
mvn -pl samples/samples-jvm/sample-jpa -am spring-boot:run
mvn -pl samples/samples-jvm/sample-feign -am spring-boot:run
mvn -pl samples/samples-jvm/sample-webflux -am spring-boot:run
```

Ports: JPA `8080`, Feign `9000`, WebFlux `8081`.
