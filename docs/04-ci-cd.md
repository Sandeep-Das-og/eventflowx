# CI/CD Architecture – EventFlowX

## 1. Overview

CI/CD is implemented using:

- GitHub Actions (CI)
- Docker (image build)
- SonarCloud (code quality)
- GitHub Container Registry (image storage)
- ArgoCD (GitOps deployment)
- Helm (Kubernetes packaging)

---

## 2. CI Pipeline Flow

On Pull Request:

1. Checkout code
2. Set up JDK 21
3. Run:
    - ./gradlew clean build
    - Unit tests
    - Integration tests
4. Generate coverage report
5. Run SonarCloud scan
6. Fail if coverage < 80%

On Merge to main:

1. Build Docker images
2. Tag with:
    - latest
    - commit SHA
3. Push to GHCR
4. Update Helm values (image tag)
5. ArgoCD auto-sync deploys

---

## 3. GitHub Actions Workflow Structure

.github/workflows/

- ci.yml
- docker-build.yml
- deploy.yml

---

## 4. SonarCloud Integration

Quality Gate Rules:

- Coverage >= 80%
- No critical vulnerabilities
- No blocker code smells

Fail build if gate fails.

---

## 5. Branch Strategy

- main → production
- develop → staging
- feature/* → feature branches

PR required before merge.

---

## 6. GitOps Deployment Flow

1. CI updates Helm chart image tag
2. Commit pushed to infra directory
3. ArgoCD detects change
4. ArgoCD syncs cluster automatically

No manual kubectl apply.
