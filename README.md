# backend

Kotlin + Spring Boot backend. Provides the REST API for the frontend's "label" feature.

## Tech stack

- **Kotlin 2.3** + **Spring Boot 4.1** (Spring Web MVC, Spring Data JPA)
- **Gradle** (Kotlin DSL) with the Gradle Wrapper included
- **Java 25** (Temurin) — set via the toolchain in `build.gradle.kts`
- Storage: **PostgreSQL 17** (Spring Data JPA / Hibernate). Tests use in-memory H2.

## Package structure (feature-based, extensibility-first)

```
com.sg.backend/
├── BackendApplication.kt      # entry point
├── config/
│   └── WebCorsConfig.kt        # CORS (allow frontend origin). Future SecurityConfig home
├── common/
│   ├── HealthController.kt      # GET /api/health
│   ├── NotFoundException.kt
│   └── ApiExceptionHandler.kt   # global exceptions -> RFC 7807 ProblemDetail
└── label/                       # "label" feature module
    ├── Label.kt                 # domain model
    ├── LabelRepository.kt       # storage interface (swap point)
    ├── LabelService.kt          # business rules such as validation
    ├── LabelController.kt       # REST endpoints
    ├── dto/LabelDtos.kt         # request/response DTOs
    └── persistence/             # persistence adapter (JPA)
        ├── LabelEntity.kt       #   @Entity (table: labels)
        ├── LabelJpaRepository.kt #  Spring Data JpaRepository
        └── JpaLabelRepository.kt #  LabelRepository implementation (adapter)
```

**Design intent**
- Feature packages (`label/`) raise cohesion; add a package as features grow.
- The service depends only on the `LabelRepository` **interface**, so swapping in-memory for Postgres/JPA leaves controller/service code untouched.
- Exceptions are handled consistently in `ApiExceptionHandler` in a standard format.
- Auth/security extends by adding a `SecurityConfig` under `config/` (not applied yet).

## Configuration files

- `application.properties` (base) and `application-prod.properties` are **committed** — they hold
  no secrets (prod uses env-var placeholders like `${DB_URL}`), and the deployed artifact needs them.
- `application-local.properties` is **git-ignored** (it may hold a local DB password). Create it from
  the template before running locally:

```bash
cd src/main/resources
cp application-local.properties.example application-local.properties
```

Real secrets (DB credentials, etc.) are always injected as **environment variables** at runtime,
never committed.

## Prerequisite: PostgreSQL

A local PostgreSQL 17 must be running (Homebrew).

```bash
brew services start postgresql@17     # start the server (also starts at login)
```

Create the DB/role once:

```bash
createdb -O sgweb sgweb                                          # DB: sgweb
psql -d postgres -c "CREATE ROLE sgweb LOGIN PASSWORD 'sgweb';"  # role: sgweb
```

Connection values in `application-local.properties` are local dev defaults; in production,
always override with the env vars `DB_URL` / `DB_USERNAME` / `DB_PASSWORD`.
Hibernate auto-creates the schema (`labels` table) via `ddl-auto=update`.

## Configuration profiles (local / prod)

Environment settings are split by profile.

| File | Purpose | Contents |
|---|---|---|
| `application.properties` | common | app name, port, common JPA. Default active profile = `local` |
| `application-local.properties` | local (dev) | local Postgres, `ddl-auto=update`, `show-sql=true` |
| `application-prod.properties` | production | connection via **env vars** (no secrets), `ddl-auto=validate` |
| `src/test/resources/application.properties` | test | in-memory H2 (`test` profile) |

- **Local run**: with nothing set, the `local` profile is applied automatically.
- **Production run**: set `SPRING_PROFILES_ACTIVE=prod` plus the DB env vars, and only `prod` applies.

## Running

### Local (development)

```bash
./gradlew bootRun            # default profile: local -> connects to local Postgres
```

Default port: **8080**

### Production

```bash
export SPRING_PROFILES_ACTIVE=prod
export DB_URL=jdbc:postgresql://<host>:5432/<db>
export DB_USERNAME=<user>
export DB_PASSWORD=<password>
export APP_CORS_ALLOWED_ORIGINS=https://your-domain.com
java -jar build/libs/backend-0.0.1-SNAPSHOT.jar
```

Production uses `ddl-auto=validate`, so the schema must already exist (manage with a migration tool).

Build:

```bash
./gradlew build             # tests run on H2 -> no local Postgres needed
```

## API

| Method | Path | Description | Success |
|---|---|---|---|
| GET | `/api/health` | health check | 200 |
| GET | `/api/labels` | list all labels (newest first) | 200 |
| POST | `/api/labels` | create a label `{ "text": "..." }` | 201 |
| DELETE | `/api/labels/{id}` | delete a label | 204 |

- empty/over-200-char text -> `400` (ProblemDetail)
- deleting a missing id -> `404` (ProblemDetail)

## Frontend integration

- The frontend calls `/api`, and during development `frontend/vite.config.ts`'s `server.proxy` forwards to this server (8080).
- Allowed CORS origins are managed via `app.cors.allowed-origins`.

## Next steps (planned)

- Introduce Flyway/Liquibase migrations (currently `ddl-auto=update`)
- Add Spring Security (login/auth) — `config/SecurityConfig`
- Strengthen request validation with Bean Validation (`spring-boot-starter-validation`)
