# Obligations Tracker Constitution

## Core Principles

### I. Amsterdam Business Dates
All business-date decisions MUST use the current date in the Europe/Amsterdam time zone,
including decisions made on the current date itself. Production code MUST obtain the current
date through a controllable clock or equivalent provider, and tests MUST set that date
explicitly. This prevents host time zones and wall-clock timing from changing business
outcomes or making tests nondeterministic.

### II. RFC 9457 Error Responses
Every error response MUST use RFC 9457 Problem Details with the
`application/problem+json` media type. This gives clients one consistent, machine-readable
error contract.

### III. ISO-8601 Date Contracts
The application MUST accept and return dates in ISO-8601 format. It MUST NOT require
locale-specific date representations in application interfaces.

### IV. Acceptance-Criteria Traceability
Feature code MUST NOT be written unless its behavior is linked to an acceptance criterion
in the feature specification. Changes MUST preserve that link so implementation and
verification remain grounded in agreed outcomes.

### V. Maven Wrapper Only
Builds and tests MUST be run through the repository's Maven wrapper. Developers and
automation MUST use `./mvnw`, not a separately installed Maven, so they use the
project-defined Maven environment.

## Date and API Contracts

Business-date calculations MUST explicitly use Europe/Amsterdam and MUST NOT depend on the
machine's default time zone. Date values crossing application interfaces MUST follow the
ISO-8601 contract. Errors, including validation and resource errors, MUST follow the RFC
9457 contract.

## Implementation and Verification

Each feature's acceptance criteria MUST be identifiable from its specification and linked
to the corresponding implementation and verification. Tests for date-dependent behavior
MUST control the current date. Build and test commands MUST use `./mvnw`.

## Governance

Amendments MUST be proposed with their rationale and an impact assessment. Project
maintainers MUST review proposed amendments for consistency with existing principles and
feature specifications before approval. An approved amendment MUST update this document's
version and last-amended date; its sync impact report MUST be reviewed and removed before
the amendment is committed.

Versioning follows semantic versioning for governance: MAJOR increments for backward-
incompatible principle or governance changes, MINOR for new principles or materially
expanded requirements, and PATCH for clarifications or non-semantic wording changes.

Implementation and code review MUST verify compliance with applicable principles.
Automated tests MUST cover verifiable requirements, including controlled business dates,
date formats, and error response contracts where applicable. Any exception MUST be
documented in the relevant feature specification and approved by project maintainers.

**Version**: 1.0.1 | **Ratified**: 2026-10-18 | **Last Amended**: 2026-10-18
