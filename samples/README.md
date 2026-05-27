# Samples

Example applications for **spring-mapper-enum**, organized as two mirrors:

| Tree | Purpose |
|------|---------|
| [samples-jvm](samples-jvm/) | Runnable on the JVM (development, tests, CI) |
| [samples-native](samples-native/) | Same features, packaged for GraalVM native image |

Each tree contains:

| Module | Demonstrates |
|--------|----------------|
| `sample-jpa` | Spring MVC, Jackson, JPA (`@MapperEnumType` + `@MapperEnumDBConverter`) |
| `sample-feign` | Spring MVC, Jackson, OpenFeign query maps (`@EnableFeignMapperEnum`) |

## Build all samples (JVM tests)

From the repository root:

```bash
mvn clean test
```

Only JVM samples:

```bash
mvn -pl samples/samples-jvm -am clean test
```

Native sample modules also run **JVM tests** in CI; native binaries are built separately (see [samples-native/README.md](samples-native/README.md)).

## Run a JVM sample

```bash
mvn -pl samples/samples-jvm/sample-jpa -am spring-boot:run
mvn -pl samples/samples-jvm/sample-feign -am spring-boot:run
```

Ports: JPA `8080`, Feign `9000`.
