# Quickstart: Regulatory Filing Obligations

## Prerequisites

- JDK 21
- The repository Maven wrapper (`./mvnw`)

## Automated verification

Run all tests through the Maven wrapper:

```bash
./mvnw test
```

The suite should verify the acceptance criteria listed below, with a fixed clock for date-dependent tests.

| Acceptance criteria | Validation |
|---|---|
| AC-001 | Create an obligation with both contact emails; assert HTTP 201, server-generated ID, effective due date, and persistence of both emails after application restart. |
| AC-002 | Submit an invalid due date, a blank owner, and a blank compliance lead; assert HTTP 400 and `application/problem+json`. |
| AC-003, AC-005, AC-006 | Set the application clock explicitly; verify at-risk, on-track, and overdue boundaries using working-day dates. |
| AC-004 | Create obligations with weekend and fixed-holiday due dates; assert roll-forward to the next working day. |
| AC-007 | Verify `ESCALATED` and both recipients for unacknowledged at-risk/overdue records; verify `ACKNOWLEDGED` after acknowledgment and `NONE` with no recipients for on-track records. |
| AC-008 | Create mixed-state obligations; assert only at-risk and overdue records are returned in ascending effective due-date order, including acknowledged obligations with `ACKNOWLEDGED`. |
| AC-009 | Request an unknown identifier; assert HTTP 404 and `application/problem+json`. |
| AC-010 | Acknowledge an escalated obligation; assert `ACKNOWLEDGED` and unchanged deadline state. An on-track obligation returns 409 and `application/problem+json`. |

## Manual API smoke test

Start the service:

```bash
./mvnw spring-boot:run
```

Create an obligation using the date shown in the example:

```bash
curl -i -X POST http://localhost:8080/obligations \
  -H 'Content-Type: application/json' \
  -d '{"dueDate":"2026-10-19","ownerEmail":"filings@example.test","complianceLeadEmail":"lead@example.test"}'
```

Use the returned `id` for `GET /obligations/{id}`, then request `GET /obligations/due-soon`. Stop and restart the service and retrieve the same ID again to verify persistence. The response shapes and error media type are defined in [contracts/openapi.yaml](./contracts/openapi.yaml); field semantics are defined in [data-model.md](./data-model.md).
