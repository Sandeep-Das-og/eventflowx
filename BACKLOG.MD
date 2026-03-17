# EventFlowX Backlog

This file tracks only pending work.  
Completed items are removed from this list.

## 1) Observability Maturity

- Define SLO/SLI baselines and error budget policy.
- Add trace-to-log correlation playbooks and incident response runbooks.

## 2) Performance and Resilience Verification

- Add load-test suite and baseline performance metrics.
- Add chaos/failure injection scenarios:
  - RabbitMQ unavailable
  - Postgres unavailable
  - downstream service latency/failure
- Validate system recovery behavior and document findings.

## 3) Security Ops

- Integrate secrets management (vault/secret store).
- Define and implement credential/key rotation policy.
- Add dependency scanning + SBOM generation in CI.

[//]: # (## 4&#41; Real Payment Gateway Integration)

[//]: # ()
[//]: # (- Replace simulated payment charge with real hosted checkout integration.)

[//]: # (- Implement gateway order/session creation + callback/webhook verification.)

[//]: # (- Persist payment state transitions &#40;`PENDING`, `AUTHORIZED`, `CAPTURED`, `FAILED`&#41;.)

[//]: # (- Add sandbox-to-live rollout checklist for merchant credentials and webhooks.)
