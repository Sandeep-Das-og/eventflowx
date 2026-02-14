# Kubernetes Deployment – EventFlowX

## 1. Cluster

Local cluster using Kind.

Why Kind?
- Free
- Lightweight
- Good for GitOps simulation

---

## 2. Namespaces

- eventflowx-core
- eventflowx-observability
- eventflowx-infra

---

## 3. Deployment Components Per Service

Each service includes:

- Deployment
- Service (ClusterIP)
- ConfigMap
- Secret
- HorizontalPodAutoscaler

---

## 4. Rolling Updates

Strategy:
- maxUnavailable: 0
- maxSurge: 1

Ensures zero downtime deployment.

---

## 5. Liveness & Readiness Probes

Liveness:
- /actuator/health

Readiness:
- /actuator/health/readiness

Prevents traffic to unhealthy pods.

---

## 6. Resource Limits

requests:
cpu: 250m
memory: 512Mi

limits:
cpu: 1
memory: 1Gi

Prevents noisy neighbor issues.

---

## 7. HPA

Metrics:
- CPU utilization 70%

Auto scales between 2–5 replicas.

---

## 8. Secret Management

Kubernetes Secrets:

- DB credentials
- JWT public key
- RabbitMQ credentials

Never commit secrets to Git.
