# Implementation Plan: Regulatory filing obligations

Status: Approved for tasking (DEC-001)
Status history: Draft -> Changes requested -> Approved for tasking
Spec: `.sdd/spec.md` | Constitution: `.specify/memory/constitution.md`

## Context

Spring Boot REST API on Java 21, built with the Maven wrapper. Empty project; no existing domain code. Persistence is an in-memory repository behind an interface, sufficient for the approved scope.

## Proposed design

| Area | Responsibility | Spec links |
|---|---|---|
| Controller and API contract | `POST /obligations`, `GET /obligations/{id}`, `GET /obligations/due-soon`; ISO-8601 dates on the wire | FR-001, FR-002, FR-003, NFR-001 |
| Service | Creates obligations, calculates state on read, builds the due-soon report | FR-001 to FR-006 |
| Deadline policy and working-day calendar | Effective due date (roll-forward); deadline state from effective due date and today | FR-004, FR-005, AC-003 to AC-006, NFR-003 |
| Escalation | Escalation state and escalation owner derived from deadline state | FR-006, AC-007 |
| Validation and error mapping | 400 and 404 as Problem Details | AC-002, AC-009, NFR-004 |
| Persistence and report query | In-memory repository; report filters AT_RISK and OVERDUE, sorts by effective due date | FR-003, AC-008 |
| Clock | Injected `Clock` in Europe/Amsterdam; fixed date in tests and the demo profile | NFR-002 |

## Affected files

- `src/main/java/com/example/obligations/calendar/WorkingDayCalendar.java`
- `src/main/java/com/example/obligations/domain/*` (obligation, deadline state, policy, escalation)
- `src/main/java/com/example/obligations/api/*` (controller, request and response, error handling)
- `src/main/java/com/example/obligations/config/ClockConfig.java`
- `src/test/java/com/example/obligations/**`

## Data flow

1. A create request is validated: due date parses as ISO-8601, owner email is not blank.
2. The effective due date is calculated once, at creation, and stored with the obligation.
3. Deadline state and escalation state are calculated on every read, because they depend on today's date. A stored state would be stale by tomorrow morning.
4. The due-soon report calculates state for every obligation and returns the AT_RISK and OVERDUE ones, earliest effective due date first.

## Test strategy

- Domain tests for the working-day calendar: weekends, fixed holidays, roll-forward
- Domain tests for deadline state: at-risk, on-track, and overdue obligations
- Boundary tests at exactly three working days (`AT_RISK`) and four working days (`ON_TRACK`), including a boundary that spans a weekend
- Controller tests for create and retrieve
- Controller test for an unknown identifier (404 Problem Details)
- A test for the due-soon report

## Traceability

| Spec ID | Plan step | Test | Status |
|---|---|---|---|
| AC-001 | Controller, Service | `ObligationDeadlineControllerTest.ac001_*` | Planned |
| AC-002 | Validation and error mapping | `ObligationDeadlineControllerTest.ac002_*` | Planned |
| AC-003 | Deadline policy | `DeadlineStatePolicyTest` boundary table (3 working days) | Planned |
| AC-004 | Working-day calendar | `WorkingDayCalendarTest`, `ObligationDeadlineControllerTest.ac004_*` | Planned |
| AC-005 | Deadline policy | `DeadlineStatePolicyTest` boundary table (4 working days) | Planned |
| AC-006 | Deadline policy | `DeadlineStatePolicyTest` (past due) | Planned |
| AC-007 | Escalation | `ObligationDeadlineControllerTest.ac007_*` | Planned |
| AC-008 | Persistence and report query | `DueSoonReportTest` | Planned |
| AC-009 | Validation and error mapping | `ObligationDeadlineControllerTest.ac009_*` | Planned |

## Risks

- Time zones: evaluating "today" in UTC shifts the date between midnight and 01:00/02:00 in Amsterdam
- Holiday data: a missing or wrong holiday silently moves effective due dates
- Date arithmetic: calendar days, hours, and working days are easy to mix up

## Open decisions

- Calculate the due-soon report in the application or in the database? No product impact; decided in T-006.
