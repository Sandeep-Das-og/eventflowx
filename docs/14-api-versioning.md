# API Versioning & Contract Policy

## Versioning Strategy

- All public gateway routes use a path-based major version prefix: `/api/v1`.
- Minor and patch changes must remain backward-compatible within the same major version.
- Breaking changes require a new major version (`/api/v2`).

## Deprecation Policy

- Unversioned endpoints are considered legacy and are supported temporarily.
- Deprecation window: 90 days from the release of a versioned equivalent.
- Deprecations are documented in release notes with migration guidance.

## Contract Change Rules

- Additive changes only within a version:
  - New fields in responses are allowed.
  - New optional request fields are allowed.
  - New endpoints under the same version are allowed.
- Breaking changes require a major version bump:
  - Removing or renaming fields.
  - Changing field types or response semantics.
  - Tightening validations in a way that rejects previously valid input.

## Documentation Requirements

- Every endpoint must have request/response examples in API docs.
- Swagger/OpenAPI must reflect the versioned gateway paths.
- Release notes must include:
  - deprecated endpoints
  - removal date
  - migration steps

## Example Paths

- `POST /api/v1/bookings`
- `GET /api/v1/events`
- `POST /api/v1/payments/charge`
- `POST /api/v1/admin/events`
