# Local Development & Setup Guide – EventFlowX

## 1. Prerequisites

Install:

- JDK 21
- Gradle
- Docker
- Kind
- kubectl
- Helm
- Node.js (for Angular)

---

## 2. Clone Repository

git clone https://github.com/your-username/eventflowx.git

cd eventflowx

---

## 3. Build Backend

./gradlew clean build

---

## 4. Start Infrastructure (Docker Compose Mode)

docker-compose up -d

Starts:
- PostgreSQL
- RabbitMQ
- Redis
- Keycloak
- Prometheus
- Grafana

---

## 5. Run Services Locally

./gradlew :services:booking-service:bootRun

Or run via Docker.

---

## 6. Run Angular

cd frontend/angular-app

npm install
ng serve

---

## 7. Run Integration Tests

./gradlew integrationTest

---

## 8. Run E2E Tests

newman run tests/newman/collection.json

robot tests/robot/

---

## 9. Run Kubernetes Mode

kind create cluster

helm install eventflowx ./infra/helm

kubectl get pods -n eventflowx-core

---

## 10. Access Dashboards

Grafana:
http://localhost:3000

Keycloak:
http://localhost:8080

RabbitMQ:
http://localhost:15672

---

## 11. Troubleshooting

If port conflict:
- Check running containers
- Stop conflicting service

If DB fails:
- Recreate container

---

## 12. Production Simulation

Push to main branch:
- CI builds
- Docker image pushed
- ArgoCD auto deploys
