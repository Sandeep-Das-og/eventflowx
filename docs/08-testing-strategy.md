# Testing Strategy – EventFlowX

---

## 1. Unit Testing

Tools:
- JUnit 5
- Mockito

Coverage requirement: 80% minimum

Test:
- Services
- Validators
- Utility classes
- Mappers

---

## 2. Integration Testing

Using Testcontainers:

- PostgreSQL container
- RabbitMQ container

Tests:
- Booking + Wallet saga flow
- DB constraints
- Idempotency behavior

---

## 3. Contract Testing (Optional Advanced)

Using:
- Spring Cloud Contract

Ensures:
API compatibility between services.

---

## 4. E2E API Testing

Tool:
Newman (Postman CLI)

Flow:
1. Login
2. Create event
3. Book seat
4. Validate wallet deduction

---

## 5. UI Testing

Tool:
Robot Framework

Tests:
- Login
- Booking flow
- Role-based rendering

---

## 6. Performance Testing (Optional)

Simulate:
1000 concurrent bookings

Validate:
- No double booking
- Latency thresholds

---

## 7. CI Pipeline Enforcement

Pipeline fails if:

- Unit tests fail
- Integration tests fail
- Coverage < threshold
- Sonar quality gate fails
