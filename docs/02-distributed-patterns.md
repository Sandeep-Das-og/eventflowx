
---

# 📄 docs/02-distributed-patterns.md

```markdown
# Distributed Patterns Used in EventFlowX

## 1. Saga Pattern (Choreography)

Used for booking + wallet + payment coordination.

Flow:

1. BookingCreated
2. WalletDebited
3. PaymentCompleted
4. BookingConfirmed

Compensation:
- WalletRefunded
- BookingCancelled

Why choreography?
- Loose coupling
- No central orchestrator
- Better scalability

---

## 2. Outbox Pattern

Problem:
If DB commit succeeds but event publish fails → inconsistency.

Solution:
- Write event to OUTBOX table
- Background publisher pushes to RabbitMQ
- Mark event as published

Ensures atomicity at service level.

---

## 3. Idempotency

Used in:
- Wallet debit
- Payment processing

Mechanism:
- Idempotency key table
- Unique constraint
- Request replay safe

---

## 4. Optimistic Locking

Used for:
- Seat reservation

Mechanism:
- Version column
- Concurrent update detection
- Retry logic

Prevents double booking.

---

## 5. Circuit Breaker

Used in:
- Booking → Payment calls
- Gateway → downstream services

States:
- Closed
- Open
- Half-open

Prevents cascading failures.

---

## 6. Retry with Backoff

Applied in:
- Event consumers
- External payment simulation

Exponential delay strategy.

---

## 7. Rate Limiting (Token Bucket)

Applied at:
- API Gateway

Prevents abuse.

---

## 8. Correlation ID

Injected at gateway.
Propagated across services.
Used in logs and traces.

---

## 9. Dead Letter Queues

For:
- Failed event consumption

Manual replay supported.
