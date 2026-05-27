# GitHub Actions

| Workflow | When | Purpose |
|----------|------|---------|
| [ci.yml](ci.yml) | Push / PR | Fast test: `mapper-enum` + `samples-jvm` on the Boot version pinned in the POMs |
| [compatibility-matrix.yml](compatibility-matrix.yml) | Weekly, manual, or called by release | `mvn test` for each Spring Boot (+ Cloud) row in the matrix |
| [release.yml](release.yml) | Tag `v*.*.*` | Matrix first, then `mvn -Pgithub deploy` |

## Extend the Boot version list

Edit `strategy.matrix.include` in `compatibility-matrix.yml`:

```yaml
include:
  - spring-boot: '4.0.6'
    spring-cloud: '2025.1.1'
  - spring-boot: '4.0.7'
    spring-cloud: '2025.1.1'
```

Use the [Spring Cloud release wiki](https://github.com/spring-cloud/spring-cloud-release/wiki/Supported-Versions) to pick a compatible `spring-cloud` BOM per Boot version.

## Boot 3.x line

Keep a separate branch (e.g. `1.x` / Boot 3) with its own matrix, or a separate workflow file that checks out that branch. Mixing Boot 3 and 4 in one reactor usually fails because of Jackson 2 vs 3.

## Native image (optional)

Native builds are slow and need GraalVM; run them manually or in a scheduled job, not on every PR:

```bash
mvn install -DskipTests
cd samples/samples-native/sample-jpa && mvn -Pnative native:compile
```

## Nexus deploy

Replace the `maven-settings-action` server id and use a repository secret for Nexus credentials instead of `GITHUB_TOKEN`.
