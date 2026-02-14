# Security Architecture – EventFlowX

## 1. Overview

Security is implemented using OAuth2 + JWT with Keycloak as Authorization Server.

We follow:

- OAuth2 Authorization Code Flow
- JWT access tokens
- Refresh tokens
- Role-based access control (RBAC)
- Token revocation support
- Gateway-level validation
- Method-level authorization

---

## 2. Authentication Flow

### Step 1 – Login

1. Angular redirects user to Keycloak
2. User authenticates
3. Keycloak returns Authorization Code
4. Frontend exchanges code for:
    - Access Token (JWT)
    - Refresh Token

---

## 3. JWT Structure

Header:
{
"alg": "RS256",
"typ": "JWT"
}

Payload:
{
"sub": "user-id",
"realm_access": {
"roles": ["USER"]
},
"exp": 1712345678
}

Signature:
Signed using RSA private key from Keycloak.

---

## 4. Token Validation

Validation happens at API Gateway:

- Verify signature using public key
- Verify expiration
- Verify audience
- Extract roles
- Inject security context

No service trusts frontend directly.

---

## 5. Role-Based Access Control

Roles:

- ADMIN
- USER
- AUDITOR

Examples:

- Only ADMIN can create events
- USER can book events
- AUDITOR can access audit logs

Spring Security usage:

@PreAuthorize("hasRole('ADMIN')")

---

## 6. Token Refresh

Access token lifetime: 5–10 minutes  
Refresh token lifetime: 30 minutes

Frontend uses silent refresh mechanism.

---

## 7. Token Revocation

If user logs out:
- Refresh token invalidated
- Access token expires naturally

Optional:
- Introspection endpoint for critical APIs

---

## 8. Multi-Tenant Design (Optional Advanced Mode)

- Multiple realms in Keycloak
- Tenant ID included in JWT
- Tenant-aware database filters

---

## 9. Gateway Security Filters

- JWT filter
- Rate limit filter
- Correlation ID filter
- CORS configuration

---

## 10. Security Threat Mitigation

| Threat | Mitigation |
|--------|-----------|
| Token tampering | RSA signature validation |
| Replay attack | Short expiry + idempotency |
| Brute force | Rate limiting |
| CSRF | SameSite cookies or token header |
| Privilege escalation | Role-based method security |
