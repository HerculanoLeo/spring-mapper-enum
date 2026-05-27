# GitHub Actions

| Workflow | When | Purpose |
|----------|------|---------|
| [ci.yml](ci.yml) | Push / PR | Fast test: `mapper-enum` + `samples` on the Boot version pinned in the POMs |
| [compatibility-matrix.yml](compatibility-matrix.yml) | Weekly, manual, or called by release | `mvn test` for each Spring Boot (+ Cloud) row in the matrix |
| [release.yml](release.yml) | Tag `v*.*.*` | Matrix first, then `mvn -Pgithub deploy -pl mapper-enum` |

## Extend the Boot version list

Edit `strategy.matrix.include` in `compatibility-matrix.yml`. Supported range on this branch: **Spring Boot 3.2.0–3.5.6**.

```yaml
include:
  - spring-boot: '3.5.6'
    spring-cloud: '2025.0.0'
  - spring-boot: '3.4.13'
    spring-cloud: '2024.0.3'
```

Use the [Spring Cloud release wiki](https://github.com/spring-cloud/spring-cloud-release/wiki/Supported-Versions) to pick a compatible `spring-cloud` BOM per Boot version:

| Spring Boot | Spring Cloud train |
|-------------|-------------------|
| 3.2.x, 3.3.x | 2023.0.x (Leyton) |
| 3.4.x | 2024.0.x (Moorgate) |
| 3.5.x | 2025.0.x (Northfields) |

## Nexus deploy

Replace the `maven-settings-action` server id and use a repository secret for Nexus credentials instead of `GITHUB_TOKEN`.
