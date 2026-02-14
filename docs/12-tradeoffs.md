# Architectural Tradeoffs – EventFlowX

---

## 1. Choreography vs Orchestration

Chosen:
Choreography

Pros:
- Decoupled
- Scalable

Cons:
- Harder debugging
- Event trace complexity

---

## 2. Eventual Consistency

Pros:
- High availability
- Loose coupling

Cons:
- Temporary inconsistency
- Complex compensation logic

---

## 3. Database Per Service

Pros:
- Independent scaling
- Clear boundaries

Cons:
- Hard reporting
- Distributed joins impossible

Solution:
Audit service + eventual aggregation.

---

## 4. Monorepo

Pros:
- Easier local dev
- Shared versioning

Cons:
- Large repo size
- Build complexity

---

## 5. Blocking Stack vs Reactive

Chosen:
Blocking (Spring MVC)

Reason:
- Simpler debugging
- Interview clarity
- Sufficient for this use case

---

## 6. RabbitMQ vs Kafka

Chosen:
RabbitMQ

Reason:
- Simpler setup
- Lightweight
- Adequate for event-driven demo

Kafka could be used in high-throughput systems.
