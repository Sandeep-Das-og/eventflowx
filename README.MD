# EventFlowX Microservice Example

A Spring Boot multi-module microservices project demonstrating :
- Domain + persistence layering
- Transactional outbox pattern
- RabbitMQ async messaging
- Idempotent consumer processing in wallet service
- API Gateway with Circuit Breaker and Retry/Backoff
- IAM/IdP with Keycloak and JWT validation at API Gateway
- Distributed tracing with Zipkin
- Local (H2) and Dev/Prod (PostgreSQL) profile strategy

## Modules

- `common`: shared domain/events/outbox components
- `services:booking-service`: booking API + outbox publisher
- `services:wallet-service`: wallet API + RabbitMQ consumer
- `services:api-gateway`: edge routing + resilience policies
- `services:event-service`: event catalog API
- `services/ui-app`: Angular + TypeScript frontend

## Architecture at a glance

1. Client calls Booking API (`POST /bookings`)
2. Booking is stored in DB
3. BookingCreated event is stored in outbox table in same transaction
4. Scheduler publishes outbox events to RabbitMQ
5. Wallet service consumes booking events
6. Wallet service enforces idempotency using `processed_events`

## Tech Stack

- Java 21
- Spring Boot 3.2.x
- Spring Data JPA
- RabbitMQ
- PostgreSQL / H2
- Spring Cloud Gateway + Resilience4j
- Micrometer Tracing + Zipkin
- JUnit 5 + Mockito + Testcontainers
- Docker + Docker Compose

## Prerequisites

- JDK 21 (`java -version`)
- Docker Desktop (required for compose and Testcontainers integration tests)
- Optional: Postman/Newman, Robot Framework

## Build

```bash
./gradlew clean build
```

Build only one module:

```bash
./gradlew :services:booking-service:build
./gradlew :services:wallet-service:build
./gradlew :services:api-gateway:build
./gradlew :services:event-service:build
```

Build UI app:

```bash
cd services/ui-app
npm install
npm run build:prod
```

## Run the App

### Option 1: Full stack with Docker Compose (recommended)

Create local env file first:

```bash
cp .env.example .env
```

Update `.env` with strong local values, then run:

```bash
./gradlew clean build
docker compose up --build
```

Services:
- API Gateway: `http://localhost:8081`
- UI App: `http://localhost:6767`
- Keycloak (IdP): `http://localhost:9090`
- RabbitMQ UI: `http://localhost:15672` (guest/guest)
- Zipkin UI: `http://localhost:9411`

Note: booking, wallet, and event services are internal-only in Docker Compose and intentionally not exposed to host ports.

Stop stack:

```bash
docker compose down -v
```

### Option 2: Run locally with Gradle (H2/local profile)

Terminal 1:

```bash
./gradlew :services:booking-service:bootRun
```

Terminal 2:

```bash
./gradlew :services:wallet-service:bootRun
```

Terminal 3:

```bash
./gradlew :services:api-gateway:bootRun
```

Terminal 4:

```bash
cd services/ui-app
npm install
npm start
```

Note: RabbitMQ still needs to be running for messaging flows.

## Spring Profiles

- `local` (default): H2 in-memory DB
- `dev`: PostgreSQL
- `prod`: PostgreSQL + stricter JPA validation

Run with a specific profile:

```bash
SPRING_PROFILES_ACTIVE=dev ./gradlew :services:booking-service:bootRun
```

## IAM / IdP (Keycloak)

- Realm: `eventflowx`
- Client: `eventflowx-ui` (public client)
- IAM role model:
  - Base roles: `user`, `admin`
  - Fine-grained roles: `event.read`, `booking.write`, `wallet.read`, `wallet.credit`
- IAM groups:
  - `customers`: mapped to user + functional roles
  - `ops-admins`: mapped to admin + functional roles
- Token/session policy:
  - Access token lifespan: 15 minutes
  - SSO idle timeout: 30 minutes
  - SSO max lifespan: 8 hours
  - Refresh token rotation enabled (`revokeRefreshToken=true`)
- Demo users:
  - `eventflowx-user` / `eventflowx-pass`
  - `eventflowx-admin` / `eventflowx-admin-pass`

Get access token:

```bash
TOKEN=$(curl -s -X POST "http://localhost:9090/realms/eventflowx/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=eventflowx-ui" \
  -d "username=eventflowx-user" \
  -d "password=eventflowx-pass" | sed -n 's/.*\"access_token\":\"\\([^\"]*\\)\".*/\\1/p')
```

Gateway authorization mapping:
- `GET /events/**` -> `event.read` or `admin`
- `POST /bookings/**` -> `booking.write` or `admin`
- `GET /wallets/**` -> `wallet.read` or `admin`
- `POST /wallets/*/credit` -> `wallet.credit` or `admin`

## Quick API Smoke Test

Create a booking through gateway:

```bash
curl -X POST http://localhost:8081/bookings \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"customerName":"alice","eventName":"concert"}'
```

Check wallet balance:

```bash
curl -H "Authorization: Bearer $TOKEN" http://localhost:8081/wallets/alice
```

Credit wallet:

```bash
curl -X POST -H "Authorization: Bearer $TOKEN" "http://localhost:8081/wallets/alice/credit?amount=100"
```

List events:

```bash
curl -H "Authorization: Bearer $TOKEN" http://localhost:8081/events
```

## Testing

### Unit Tests

Run all unit tests:

```bash
./gradlew test
```

Run module unit tests:

```bash
./gradlew :services:booking-service:test
./gradlew :services:wallet-service:test
./gradlew :services:api-gateway:test
./gradlew :services:event-service:test
```

Run UI unit tests:

```bash
cd services/ui-app
npm test
```

### Integration Tests

Integration tests use Spring Boot + Testcontainers and require Docker.

Run integration-heavy modules:

```bash
./gradlew :services:booking-service:test :services:wallet-service:test
```

If Docker is not available, Testcontainers tests are auto-skipped.

### Testcontainers Notes

- Booking integration test uses PostgreSQL + RabbitMQ containers.
- Wallet integration test validates idempotent event consumption behavior.

## Newman Tests (Postman Collection)

If you maintain Postman tests, keep files in:
- `tests/newman/EventFlowX.postman_collection.json`
- `tests/newman/local.postman_environment.json`

Run with Newman:

```bash
newman run tests/newman/EventFlowX.postman_collection.json \
  -e tests/newman/local.postman_environment.json
```

Optional HTML report:

```bash
newman run tests/newman/EventFlowX.postman_collection.json \
  -e tests/newman/local.postman_environment.json \
  -r cli,htmlextra
```

## Robot Framework Tests

If you maintain Robot tests, keep files in:
- `tests/robot/`

Run:

```bash
robot -d reports tests/robot
```

Run a specific suite:

```bash
robot -d reports tests/robot/booking_flow.robot
```

## Observability

- Trace/span IDs are included in service log patterns.
- Zipkin endpoint is configurable via `ZIPKIN_ENDPOINT`.
- Actuator endpoints exposed: `health`, `info`, `metrics`, `prometheus`.

## Resilience

Gateway provides:
- Circuit breaker for booking, wallet, and event routes
- Retry with exponential backoff
- Fallback endpoints:
  - `/fallback/bookings`
  - `/fallback/wallets`
  - `/fallback/events`

## Project Structure

```text
.
├── common
├── services
│   ├── api-gateway
│   ├── booking-service
│   ├── wallet-service
│   ├── event-service
│   └── ui-app
├── docker
│   └── postgres/init.sql
├── docker-compose.yml
└── docs
```

## CI/CD Suggestions

A practical pipeline order:
1. `./gradlew clean test`
2. `./gradlew build`
3. Build/push Docker images
4. Run compose smoke test
5. Publish test + coverage reports

## What Else Can We Include?

Good additions for a production-ready README and repo:
- API contract section with request/response examples per endpoint
- Sequence diagram for booking -> outbox -> RabbitMQ -> wallet flow
- Failure-mode matrix (duplicate event, broker down, DB down, partial failure)
- Migration strategy (`Flyway` or `Liquibase`) and rollback notes
- Security model (authn/authz, secrets strategy, TLS, mTLS)
- Performance baseline and load-test instructions (k6/JMeter/Gatling)
- SLO/SLI definitions and alerting strategy
- Contribution guidelines + coding standards + commit conventions
- Release/versioning policy and changelog workflow
- Troubleshooting playbook (common errors and fixes)

## Useful Docs

See:
- `docs/01-architecture.MD`
- `docs/02-distributed-patterns.md`
- `docs/06-observability.md`
- `docs/08-testing-strategy.md`
- `docs/13-local-setup.md`
