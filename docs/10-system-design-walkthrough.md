# System Design Walkthrough – Booking Flow

## Scenario

User books 2 seats for an event.

---

## Step 1 – API Request

Frontend → API Gateway
Gateway:
- Validates JWT
- Adds Correlation ID
- Forwards to Booking Service

---

## Step 2 – Seat Reservation

Booking Service:
- Fetch seat
- Optimistic lock update
- Status → RESERVED
- Publish BookingCreated event

---

## Step 3 – Wallet Debit

Wallet Service:
- Listen BookingCreated
- Deduct balance
- Insert ledger entry
- Publish WalletDebited

If insufficient funds:
- Publish WalletDebitFailed

---

## Step 4 – Payment Processing

Payment Service:
- Listen WalletDebited
- Simulate provider
- Publish PaymentCompleted

---

## Step 5 – Booking Confirmation

Booking Service:
- Listen PaymentCompleted
- Mark CONFIRMED

---

## Failure Scenario

If WalletDebitFailed:
- Booking Service marks CANCELLED

If Payment fails:
- Wallet refunds
- Booking cancels

---

## CAP Theorem Discussion

We choose:
- Availability
- Partition tolerance

Sacrifice:
- Strong consistency across services

Compensation ensures eventual consistency.
