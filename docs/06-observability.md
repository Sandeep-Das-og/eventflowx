# Observability – EventFlowX

Stack:

- Prometheus (metrics)
- Grafana (visualization)
- Loki (logs)
- OpenTelemetry (tracing)
- Jaeger (trace UI)

---

## 1. Metrics

Using Micrometer in Spring Boot.

Custom metrics:

- booking_requests_total
- wallet_debit_failures
- payment_latency
- seat_conflict_count

---

## 2. Logging

Structured JSON logging.

Fields:
- timestamp
- service
- correlationId
- userId
- eventType

---

## 3. Distributed Tracing

Flow:

Gateway → Booking → Wallet → Payment → Booking

Trace ID propagated via headers.

---

## 4. Dashboards

Grafana dashboards include:

- Request latency
- Error rate
- Saga success/failure ratio
- CPU & memory usage
- Message queue depth

---

## 5. Alerting (Optional Advanced)

Alert rules:

- Error rate > 5%
- Pod restart count > 3
- Payment latency > threshold

---

## 6. Why Observability Matters

Without tracing:
- Saga debugging becomes impossible
- Root cause analysis is slow

Observability reduces MTTR significantly.
