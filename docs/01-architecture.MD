# EventFlowX – System Architecture

## 1. Overview

EventFlowX is a distributed, event-driven microservices platform for:

- Event discovery and seat booking
- Wallet-based payments
- Saga-driven distributed transaction handling
- Role-based secure access
- Observability-first design

This system simulates a production-grade architecture similar to BookMyShow + Paytm.

---

## 2. Architectural Style

- Microservices architecture
- Database per service pattern
- Event-driven communication via RabbitMQ
- REST APIs for synchronous calls
- Saga (Choreography)
- CQRS (partial usage)
- API Gateway pattern
- GitOps deployment model

---

## 3. High-Level Architecture Diagram

                ┌──────────────────────┐
                │      Angular UI      │
                └───────────┬──────────┘
                            │
                     ┌──────▼──────┐
                     │ API Gateway │
                     └──────┬──────┘
                            │
    ┌───────────────┬───────────────┬───────────────┐
    ▼               ▼               ▼               ▼
User Service   Event Service   Booking Service   Wallet Service
│               │
▼               ▼
Payment Service  Notification Service
│
▼
Audit Service

All services communicate asynchronously via RabbitMQ.
Each service owns its own PostgreSQL database.
