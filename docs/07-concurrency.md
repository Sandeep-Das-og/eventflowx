# Concurrency & Multithreading – EventFlowX

This system includes multiple concurrency challenges.

---

## 1. Seat Double Booking Problem

Scenario:
Two users try booking the same seat simultaneously.

Solution:
Optimistic locking with version column.

Example:

@Version
private Long version;

If update fails:
- Retry up to 3 times
- If still fails → reject booking

---

## 2. Wallet Double Spend Prevention

Problem:
Two concurrent debit requests.

Solution:
- Idempotency key
- Unique constraint
- Transactional ledger update

---

## 3. Custom Executor Service

Booking Service uses:

ThreadPoolExecutor(
corePoolSize = 10,
maxPoolSize = 50,
queueCapacity = 500
)

Used for async event processing.

---

## 4. Deadlock Demonstration (Educational)

Simulate:

Thread A:
lock wallet
lock booking

Thread B:
lock booking
lock wallet

Demonstrates why lock ordering matters.

---

## 5. CompletableFuture Usage

Used for:
- Parallel fetching event details
- Payment simulation

Example:
CompletableFuture.supplyAsync(...)

---

## 6. Blocking vs Non-Blocking

RabbitMQ consumers are asynchronous.
DB operations remain blocking (JDBC).

Discussion:
When to move to reactive stack?

---

## 7. Rate Limiter Implementation

Token Bucket algorithm:

- ConcurrentHashMap for user buckets
- Scheduled refill
- Atomic operations

Thread-safe design required.

---

## 8. Thread Pool Tuning Discussion

Questions you should answer:

- What happens if queue is full?
- How to handle backpressure?
- Why not unbounded queue?
- How to size pool based on CPU cores?

---

## 9. Race Condition Simulation

Integration test simulates 100 concurrent booking requests.

Expected:
Only one booking succeeds for same seat.
