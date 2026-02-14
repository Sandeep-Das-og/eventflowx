# Failure Scenarios & Recovery Strategies – EventFlowX

This document describes real-world distributed system failures
and how EventFlowX handles them.

---

## 1. Booking Created but Wallet Service Down

### Scenario

1. Booking Service publishes BookingCreated
2. Wallet Service is unavailable
3. Event remains unprocessed

### Handling

- RabbitMQ persists message
- Message waits in queue
- Wallet Service consumes when back online

System remains eventually consistent.

---

## 2. Wallet Debited but Payment Fails

### Scenario

1. BookingCreated → WalletDebited
2. Payment processing fails

### Handling

Payment Service publishes PaymentFailed

Booking Service:
- Marks booking CANCELLED

Wallet Service:
- Listens to PaymentFailed
- Refunds wallet
- Publishes WalletRefunded

Compensation completed.

---

## 3. Event Published but Service Crashes Before Acknowledgement

### Scenario

Service processes event but crashes before ACK.

### Handling

- RabbitMQ re-delivers message
- Idempotency ensures safe reprocessing
- Deduplication table prevents double debit

---

## 4. Duplicate Event Delivery

RabbitMQ guarantees at-least-once delivery.

Problem:
Event may be delivered twice.

Solution:
- Idempotency keys
- Unique DB constraint
- Process once only

---

## 5. Network Partition Between Services

Booking cannot reach Wallet.

Strategy:

- Circuit breaker opens
- Retry with backoff
- Fail fast if threshold reached

Avoids cascading failure.

---

## 6. Database Failure

If PostgreSQL crashes:

- Service becomes unhealthy
- Readiness probe fails
- Kubernetes removes pod from load balancer
- Auto restart triggered

---

## 7. Partial Saga Completion

Example:

BookingCreated → WalletDebited → Crash before PaymentCompleted

Recovery:

- Event replay from queue
- Saga resumes
- Compensation if required

---

## 8. Outbox Publisher Failure

If event not published:

- Outbox table retains event
- Background scheduler retries publish
- Ensures no lost events

---

## 9. Dead Letter Queue Handling

If message fails N times:

- Sent to DLQ
- Logged by Audit Service
- Manual replay supported

---

## 10. High Traffic Surge (Flash Sale Scenario)

Problem:
1000 concurrent bookings for same seat.

Solution:

- Optimistic locking
- Retry limited attempts
- Rate limiter at gateway
- Horizontal pod autoscaling

Only one booking succeeds.

---

## 11. Keycloak Down

If auth server unavailable:

- Existing JWT tokens still valid
- New login fails
- System partially operational

---

## 12. Message Broker Crash

RabbitMQ restart:

- Durable queues restore messages
- Consumers reconnect
- No message loss

---

## 13. Corrupted Event Schema

If event version changes:

- Version field included in event payload
- Consumer handles backward compatibility
- Reject unknown version to DLQ

---

## 14. Memory Leak Scenario

If service memory increases:

- Liveness probe fails
- Kubernetes restarts pod
- Observability alert triggers

---

## 15. Rolling Deployment During Active Traffic

If new version deployed:

- Old pods remain until new ready
- Zero downtime
- In-flight requests complete

---

## Interview Discussion Topics

You must explain:

- How to detect incomplete sagas?
- How to replay failed events?
- How to avoid infinite retry loops?
- How to handle poison messages?
- How to ensure financial integrity?

This document is critical for senior-level interviews.
