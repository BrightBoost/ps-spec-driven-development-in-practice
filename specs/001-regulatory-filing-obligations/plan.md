# Implementation Plan: Regulatory Filing Obligations

Status: Approved for tasking (DEC-002)
Status history: Draft -> Changes requested -> Approved for tasking (DEC-001) -> Re-approved for tasking (DEC-002)

**Branch**: `001-regulatory-filing-obligations` | **Date**: 2026-10-18 | **Spec**: [spec.md](./spec.md)

**Input**: Approved feature specification at `specs/001-regulatory-filing-obligations/spec.md`

## Summary

Deliver a Spring Boot REST API for creating, retrieving, and listing regulatory filing obligations. Persist each obligation and its calculated effective due date in a file-backed H2 database so records survive application restarts. Calculate deadline and escalation states from the current Europe/Amsterdam date at read time, using a controllable clock and the fixed 2026–2027 demo holiday calendar. Return both contact emails as escalation recipients for AT_RISK and OVERDUE obligations; ON_TRACK obligations have no recipients.

## Technical Context

**Language/Version**: Java 21

**Primary Dependencies**: Spring Boot 4.0.6; existing `spring-boot-starter-webmvc`; add `spring-boot-starter-jdbc` and H2 for durable embedded storage

**Storage**: File-backed H2 database; persist obligation ID, requested due date, owner email, compliance lead email, and effective due date. Keep the database outside source control.

**Testing**: JUnit 5 and Spring Boot Test; all builds and tests via `./mvnw`

**Target Platform**: Java server application running on a JDK 21-compatible platform

**Project Type**: Single-project Spring Boot web service

**Performance Goals**: No quantitative performance target is specified.

**Constraints**: ISO-8601 date inputs/outputs; Europe/Amsterdam business-date decisions; controllable clock in tests; fixed 2026–2027 demo holidays; RFC 9457 Problem Details; no caller authentication or notifications in feature scope; records survive restarts.

**Scale/Scope**: One obligation type and one Maven application; no volume or concurrency target specified.

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| Principle | Plan constraint | Gate |
|---|---|---|
| I. Amsterdam Business Dates | Inject a controllable `Clock`; derive business `LocalDate` values in `Europe/Amsterdam`; tests set the clock explicitly. | PASS |
| II. RFC 9457 Error Responses | Return `application/problem+json` for validation and not-found errors. | PASS |
| III. ISO-8601 Date Contracts | Use ISO-8601 calendar dates at API boundaries. | PASS |
| IV. Acceptance-Criteria Traceability | Link endpoint, date-state, error, persistence, and report tests to their AC IDs. | PASS |
| V. Maven Wrapper Only | Run verification through `./mvnw`. | PASS |

No gate violations identified.

## Test strategy

- Boundary tests at exactly three working days (`AT_RISK`) and four working days (`ON_TRACK`), including a boundary that spans a weekend
- Deadline state tests for overdue obligations and for an obligation due today
- Controller tests for create, retrieve and the unknown-identifier 404
- Create validation tests for blank owner and compliance lead emails
- Escalation response tests for AT_RISK and OVERDUE recipients, and ON_TRACK with no recipients

## Traceability

| Spec ID | Plan step | Implementation | Unit test | Controller test | Manual request | Status |
|---|---|---|---|---|---|---|
| AC-001 | Controller, Service | `ObligationService.create` | `ObligationServiceTest.ac001_*` | `ObligationDeadlineControllerTest.ac001_*` | | Done |
| AC-002 | Validation and error mapping | `ObligationService.create`, `ApiExceptionHandler` | `ObligationServiceTest.ac002_*` | `ObligationDeadlineControllerTest.ac002_*` | | Done |
| AC-003 | Deadline policy | `DeadlineStatePolicy.stateFor` | `DeadlineStatePolicyTest` (3 working days) | `ObligationDeadlineControllerTest.ac003_*` | | Done |
| AC-004 | Working-day calendar | `WorkingDayCalendar.onOrNextWorkingDay` | `WorkingDayCalendarTest`, `DeadlineStatePolicyTest.ac004_*` | `ObligationDeadlineControllerTest.ac004_*` | | Done |
| AC-005 | Deadline policy | `DeadlineStatePolicy.stateFor` | `DeadlineStatePolicyTest` (4 working days) | | | Done |
| AC-006 | Deadline policy | `DeadlineStatePolicy.stateFor` | `DeadlineStatePolicyTest` (past due) | `ObligationDeadlineControllerTest.ac006_*` | | Done |
| AC-007 | Escalation (recipients, DEC-002) | `ObligationService.view` | `ObligationServiceTest.ac007_*` | `ObligationDeadlineControllerTest.ac007_*` | | Done |
| AC-008 | Persistence and report query | `ObligationService.dueSoon` | `ObligationServiceTest.ac008_*` | `DueSoonReportTest` | | Done |
| AC-009 | Validation and error mapping | `ApiExceptionHandler.notFound` | `ObligationServiceTest.ac009_*` | `ObligationDeadlineControllerTest.ac009_*` | | Done |

## Phase 0: Research

See [research.md](./research.md). The persistence choice is limited to the confirmed restart-survival requirement and the existing single-service Spring Boot project; it does not introduce an external service or deployment dependency.

## Phase 1: Design

- [Data model](./data-model.md): persisted obligation fields, derived response state and recipients, calendar rules, and validation.
- [OpenAPI contract](./contracts/openapi.yaml): create, retrieve, due-soon, and RFC 9457 error responses.
- [Quickstart](./quickstart.md): Maven-wrapper verification and end-to-end API checks, including restart persistence.

### Constitution Re-check

The design retains explicit Amsterdam-zone date evaluation and controlled test clocks; uses ISO-8601 API dates and RFC 9457 errors; maps acceptance criteria to tests; and uses only `./mvnw` for Maven verification. **PASS — no violations.**

## Project Structure

### Documentation (this feature)

```text
specs/001-regulatory-filing-obligations/
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
└── contracts/
    └── openapi.yaml
```

### Source Code (repository root)

```text
src/
├── main/
│   ├── java/com/example/obligations/
│   │   ├── api/            # REST controller and request/response types
│   │   ├── calendar/       # Working-day calendar and fixed demo holidays
│   │   ├── config/         # Clock and wiring
│   │   ├── domain/         # Obligation, states, deadline policy, service, JDBC repository
│   │   └── ObligationsTrackerApplication.java
│   └── resources/
│       ├── application.properties
│       └── static/         # due-soon report page
└── test/
    └── java/com/example/obligations/
        ├── (root)          # HTTP contract and error tests (AC-001, AC-002, AC-008, AC-009)
        ├── calendar/       # working-day tests (AC-003, AC-004)
        └── domain/         # restart/durable storage, state, and escalation tests (AC-001, AC-003–AC-007)
```

**Structure Decision**: Extend the existing single Maven/Spring Boot application and its `com.example.obligations` package. Keep the API, calendar, and domain logic (including the JDBC repository, wired in `config/`) separated within that project; the only UI is a static due-soon report page served by the same application; add no separate frontend or deployable.

## Complexity Tracking

No constitution violations or additional project boundaries require justification.
