# School API

Spring Boot REST API for managing schools, users, lessons, and lesson units.
The application uses PostgreSQL for persistence, Flyway for schema migrations,
Spring Data JPA for repositories, and Spring MVC controllers for HTTP routes.

## Related Repository

Frontend repository: [school-frontend](https://github.com/MayPyone/school-frontend)

## Project Layout

```text
src/main/java/com/school/school
|-- SchoolApplication.java       # Spring Boot entry point
|-- controller/                  # HTTP route handlers
|-- service/                     # Business logic and entity/DTO mapping
|-- repository/                  # Spring Data JPA repositories
|-- entity/                      # JPA entities and enums
`-- pojo/                        # Request and response records

src/main/resources
|-- application.properties       # PostgreSQL, JPA, and Flyway settings
`-- db/migration/                # Flyway database migrations
```

## Main Concepts

- `User`: a person who can register/login and has one of the roles `ADMIN`,
  `TEACHER`, or `ASSISTANT`.
- `School`: school profile data owned by a user.
- `Lesson`: content belonging to a school, assigned to a level and created by a
  user.
- `Unit`: smaller lesson content item that belongs to a lesson.
- `Level`: lookup table seeded by the first migration with `Basic`,
  `Intermediate`, and `Advanced`.
- `OpeningHour`, `ClassSchedule`, `Staff`, and `Activity`: persisted domain
  entities that currently do not have public controllers.

## Request Flow

1. Controllers define the REST endpoints and deserialize request bodies.
2. Services load referenced entities, apply changes, and map entities to
   response records.
3. Repositories provide database access through Spring Data JPA.
4. Flyway migrations define and evolve the PostgreSQL schema.

There is no security layer or token-based authentication in this codebase yet.
Login currently validates a plain-text password and returns the `User` entity.

## Running Locally

Start PostgreSQL:

```bash
docker compose up -d db
```

Run the application:

```bash
./gradlew bootRun
```

The configured database connection is:

```text
jdbc:postgresql://localhost:54325/school
username: postgres
password: pass
```

## API Documentation

Endpoint documentation is maintained in [docs/API.md](docs/API.md).

If the app is running locally, Springdoc is also on the classpath. Depending on
the Spring Boot/   Springdoc compatibility in your environment, OpenAPI UI may be
available at:

```text
http://localhost:8080/swagger-ui/index.html
```
