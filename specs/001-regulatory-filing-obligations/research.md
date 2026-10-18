# Research: Regulatory Filing Obligations

## Durable obligation storage

**Decision**: Use a file-backed H2 database through Spring JDBC. Persist the server-generated identifier, submitted due date, owner email, and effective due date. Keep deadline and escalation states derived at read time.

**Rationale**: The approved clarification requires records to survive application restarts. H2 provides an embedded relational store that can run with the existing single Spring Boot service and does not require a separately operated database. JDBC keeps the persistence layer small for the feature’s single entity and basic create/read/list operations. Persisting the effective date with the submitted values makes the accepted calendar calculation durable as part of the obligation record.

**Alternatives considered**:
- In-memory storage: rejected because it loses obligations on restart.
- Flat-file serialization: possible, but would require bespoke update, concurrent-access, and data-integrity behavior that a small relational store already provides.
- External database service: rejected because the feature specifies no external service or deployment dependency.

## API shape

**Decision**: Expose JSON REST operations as `POST /obligations`, `GET /obligations/{id}`, and `GET /obligations/due-soon`.

**Rationale**: These operations directly represent the specified create, identifier lookup, and due-soon report journeys. Use HTTP 201 for creation, 200 for successful reads, 400 for invalid create requests, and 404 for an unknown identifier. Represent errors using RFC 9457 Problem Details.

**Alternatives considered**:
- A separate reporting resource or query parameter: unnecessary for the single specified report.
- Authentication and notification endpoints: explicitly outside the feature scope.

## Business-date calculations

**Decision**: Base every current-date decision on `LocalDate.now(clock.withZone(ZoneId.of("Europe/Amsterdam")))`; inject the clock into business logic and fix it explicitly in tests. Treat weekdays other than the specified 2026–2027 demo holidays as working days.

**Rationale**: This follows the constitution and makes tests deterministic. Calculate the effective due date by advancing a non-working due date until a working day is reached. Calculate deadline state with overdue taking precedence for dates before today, then `AT_RISK` for zero through three working days away, otherwise `ON_TRACK`. Derive escalation as `ESCALATED` for `AT_RISK` or `OVERDUE` and `NONE` otherwise.

**Alternatives considered**:
- Host-default timezone or system clock access in business logic: rejected because results would vary by environment and be difficult to test.
- Persisting deadline/escalation states: rejected because they depend on the current date and would become stale.

## Unspecified operational targets

**Decision**: Do not invent performance, availability, throughput, data-volume, rate-limit, or concurrent-edit guarantees.

**Rationale**: The feature scope and acceptance criteria set no such thresholds. The implementation should satisfy the explicitly defined behavior without claiming unapproved service levels.

**Alternatives considered**:
- Adding numeric targets: rejected because doing so would expand the approved requirements without evidence.
