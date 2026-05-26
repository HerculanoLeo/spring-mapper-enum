# samples-native

GraalVM native counterparts of [samples-jvm](../samples-jvm/). Application source mirrors the JVM modules (keep in sync when changing behavior); POMs add `native-maven-plugin` and omit `spring-boot-devtools`.

## Modules

| Module | JVM mirror | Native artifact |
|--------|------------|-----------------|
| `sample-jpa` | `samples-jvm/sample-jpa` | `spring-mapper-enum-sample-native-jpa` |
| `sample-feign` | `samples-jvm/sample-feign` | `spring-mapper-enum-sample-native-feign` |

## JVM tests (CI)

Native modules run the same integration tests on the JVM:

```bash
mvn -pl samples/samples-native -am clean test
```

## Native executable

Requires [GraalVM](https://www.graalvm.org/downloads/) with `native-image`.

Install JVM artifacts first (**do not** use `-Pnative` on the full reactor — the library has no `main`):

```bash
mvn install -DskipTests
```

Then build one application:

```bash
cd samples/samples-native/sample-jpa
mvn -Pnative native:compile
```

```bash
cd samples/samples-native/sample-feign
mvn -Pnative native:compile
```

Binaries are written under each module’s `target/` directory.

## Native container image (optional)

```bash
cd samples/samples-native/sample-jpa
mvn -Pnative spring-boot:build-image
```
