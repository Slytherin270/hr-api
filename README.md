# HR Management System API

Production-ready HR API built with Java 21, Spring Boot 3.x, Spring Modulith, and Spring Data JDBC.

## Architecture Overview
- **Modular monolith** using Spring Modulith with explicit module boundaries.
- **DDD + Hexagonal**: each module has `domain`, `application`, `infra`, and `api` packages.
- **PostgreSQL** via Spring Data JDBC + Flyway for schema migrations.
- **Redis** for caching and distributed locks (idempotency).
- **JWT security** with RBAC roles (ADMIN, MANAGER, EMPLOYEE).
- **Observability** with Actuator + Micrometer (Prometheus-ready).

## Module Map
- **shared**: common primitives (Money, TimeProvider), error handling, config
- **identity**: employees, departments, authentication, authorization
- **attendance**: check-in/out and attendance status
- **leave**: leave requests, approvals, balances
- **payroll**: payroll runs, payslips
- **payment**: salary payment execution
- **notification**: event-driven notifications

## How to Run
### Local (Docker)
```bash
docker-compose up --build
```

### Local (Maven)
```bash
./mvnw spring-boot:run
```

Default admin user is seeded on startup:
- **email**: `admin@hr.local`
- **password**: `admin123`

## Example Requests
### Login
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"admin@hr.local","password":"admin123"}'
```

### Create Department
```bash
curl -X POST http://localhost:8080/api/v1/departments \
  -H 'Authorization: Bearer <token>' \
  -H 'Content-Type: application/json' \
  -d '{"name":"Engineering"}'
```

### Check-In
```bash
curl -X POST http://localhost:8080/api/v1/attendance/check-in \
  -H 'Authorization: Bearer <token>'
```

### Run Payroll
```bash
curl -X POST "http://localhost:8080/api/v1/payroll/run?month=2024-05" \
  -H 'Authorization: Bearer <token>'
```

## How to Extend
To add a **Performance Reviews** module:
1. Create `performance` module with `domain/application/infra/api` packages.
2. Define aggregates (ReviewCycle, Review, Rating) in `domain`.
3. Expose module API via `@NamedInterface` to integrate with `identity` for employee lookups.
4. Add Flyway migration for review tables.
5. Add REST endpoints under `/api/v1/reviews`.
6. Update module diagram by running Modulith tests.

## Module Boundaries
Module boundaries are enforced via Spring Modulith tests (`ModulithTests`).

## OpenAPI
Swagger UI is available at `http://localhost:8080/swagger-ui/index.html`.
