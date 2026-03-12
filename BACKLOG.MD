# EventFlowX Backlog

This file tracks only pending work.  
Completed items are removed from this list.

## 1) CI/CD Completion

- Build pipeline stages: compile, unit tests, integration tests.
- Add Newman and Robot execution stages.
- Add container image build and push stages.
- Add vulnerability scanning gates.
- Add merge quality gates for test/security thresholds.

## 2) Observability Maturity

- Create dashboards for service health, latency, error rates, and throughput.
- Define and wire alerts (critical/warning) for key failure scenarios.
- Define SLO/SLI baselines and error budget policy.
- Add trace-to-log correlation playbooks and incident response runbooks.

## 3) Performance and Resilience Verification

- Add load-test suite and baseline performance metrics.
- Add chaos/failure injection scenarios:
  - RabbitMQ unavailable
  - Postgres unavailable
  - downstream service latency/failure
- Validate system recovery behavior and document findings.

## 4) Security Ops

- Integrate secrets management (vault/secret store).
- Define and implement credential/key rotation policy.
- Add dependency scanning + SBOM generation in CI.

## 5) Real Payment Gateway Integration

- Replace simulated payment charge with real hosted checkout integration.
- Implement gateway order/session creation + callback/webhook verification.
- Persist payment state transitions (`PENDING`, `AUTHORIZED`, `CAPTURED`, `FAILED`).
- Add sandbox-to-live rollout checklist for merchant credentials and webhooks.
