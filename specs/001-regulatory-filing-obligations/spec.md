# Feature Specification: Regulatory Filing Obligations

**Feature Branch**: `recording-m2c1`

**Created**: 2026-10-18

**Status**: Approved for planning

**Input**: User description: "Use .sdd/spec.md as the approved specification for the regulatory filing obligations feature. Preserve every FR-, EC-, NFR-, and AC-ID and all thresholds and exclusions. Do not add scope."

## Clarifications

### Session 2026-10-18

- Q: What should happen to created obligations after the tracker restarts? → A: Obligations remain available after restarts.
- Q: Should the tracker reject an owner email that is nonblank but has an invalid email format? → A: Reject blank emails only; do not validate the format of nonblank values.
- Q: Should callers have to authenticate before creating, retrieving, or listing obligations? → A: No authentication is required by this feature; deployment-level access restrictions are outside scope.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Create and review a filing obligation (Priority: P1)

As a lawyer on the compliance team, I can record a regulatory filing's due date and owner email, then review its effective due date, deadline state, and escalation state so that I can see whether it needs attention.

**Why this priority**: Creating and reviewing an obligation is the core value of the tracker.

**Independent Test**: Create an obligation with a valid due date and nonblank owner email, then retrieve it by its server-assigned identifier and check the returned dates and states.

**Acceptance Scenarios**:

1. **Given** a valid due date and nonblank owner email, **When** the obligation is created, **Then** it is stored and returned with status 201, a server-assigned identifier, and its effective due date.
2. **Given** an obligation whose effective due date is exactly three working days away, **When** it is retrieved, **Then** its deadline state is `AT_RISK`.
3. **Given** an obligation whose due date is on a weekend or fixed demo holiday, **When** it is created, **Then** its effective due date is the next working day.

---

### User Story 2 - Identify obligations needing attention (Priority: P2)

As a lawyer on the compliance team, I can request a due-soon report to see obligations that are at risk or overdue, ordered by effective due date, so that I can prioritize filings.

**Why this priority**: The report provides a direct way to identify and prioritize obligations needing attention.

**Independent Test**: Create obligations in at-risk, overdue, and on-track states, request the report, and verify that only at-risk and overdue obligations are returned in earliest-effective-due-date-first order.

**Acceptance Scenarios**:

1. **Given** obligations in at-risk, overdue, and on-track states, **When** the due-soon report is requested, **Then** all at-risk and overdue obligations are returned, earliest effective due date first, and on-track obligations are excluded.
2. **Given** an obligation that is at risk or overdue, **When** it is retrieved, **Then** its escalation state is `ESCALATED` and its escalation owner is the owner email.

---

### User Story 3 - Receive clear responses for invalid requests (Priority: P3)

As a caller of the tracker, I receive a clear error response when a filing request is invalid or an obligation does not exist, so that I can distinguish rejected requests from successful ones.

**Why this priority**: Clear rejection behavior makes obligation creation and lookup outcomes unambiguous.

**Independent Test**: Submit an invalid due date, a blank owner email, and a nonexistent obligation identifier; verify the specified error statuses and Problem Details bodies.

**Acceptance Scenarios**:

1. **Given** an invalid due date or blank owner email, **When** an obligation is created, **Then** the request is rejected with status 400 and a Problem Details body.
2. **Given** an unknown obligation identifier, **When** the obligation is requested, **Then** the response has status 404 and a Problem Details body.

### Edge Cases

- `EC-001`: The due date is today
- `EC-002`: The effective due date is exactly three working days away
- `EC-003`: The effective due date is four working days away
- `EC-004`: The due date falls on a weekend
- `EC-005`: The due date falls on a fixed demo holiday
- `EC-006`: The effective due date is in the past
- `EC-007`: The due date is not a valid date
- `EC-008`: The owner email is blank
- `EC-009`: The obligation identifier does not exist

## Scope

One regulatory filing obligation has one due date, one owner email, one effective due date, and is at risk from three working days before its effective due date.

| | |
|---|---|
| **In scope** | One obligation type: a regulatory filing obligation. Create an obligation with a due date and an owner email. Calculate an effective due date: a due date on a weekend or a fixed demo holiday rolls forward to the next working day (Europe/Amsterdam). An obligation is at risk from three working days before its effective due date. A due-soon report of obligations that need attention. Escalation as a visible state on the obligation. |
| **Out of scope** | Recurring obligations. Automatic email delivery or any other notification. Multiple escalation levels. User-configurable calendars. A configurable at-risk threshold per obligation. Role-based administration. Caller authentication (deployment-level access restrictions are outside this feature). |
| **Actors** | A lawyer on the compliance team (product owner and main user). Developers maintaining the tracker. |
| **Inputs** | Due date. Owner email. |
| **Outputs** | Obligation with identifier, due date, effective due date, deadline state, and escalation state. Due-soon report. |

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The system shall create a regulatory filing obligation from a due date and a nonblank owner email, retain it across application restarts, and not validate the format of a nonblank owner email
- **FR-002**: The system shall retrieve an obligation by its server-assigned identifier
- **FR-003**: The system shall list obligations that are at risk or overdue, sorted by effective due date, earliest first
- **FR-004**: The system shall calculate an effective due date using Europe/Amsterdam working-day rules
- **FR-005**: The system shall expose a deadline state of `ON_TRACK`, `AT_RISK`, or `OVERDUE`
- **FR-006**: The system shall expose escalation as a visible state and shall not send notifications
- **FR-007**: The system shall reject an unknown obligation identifier with a not-found error

### Non-functional Requirements

- **NFR-001**: The API accepts and returns dates in ISO-8601 format
- **NFR-002**: Dates are evaluated in Europe/Amsterdam, and the current date can be controlled in tests
- **NFR-003**: The working-day calendar uses a fixed list of demo holidays for 2026 and 2027
- **NFR-004**: Errors use RFC 9457 Problem Details (`application/problem+json`)
- **NFR-005**: All tests run through the Maven wrapper

### Acceptance Criteria

`AC-001`
WHEN a create request contains a valid due date and nonblank owner email,
THE SYSTEM SHALL store the obligation and return status 201 with a server-assigned identifier and the effective due date.

`AC-002`
IF a create request contains an invalid due date or a blank owner email,
THEN THE SYSTEM SHALL reject the request with status 400 and a Problem Details body.

`AC-003`
WHILE the effective due date of an obligation is between zero and three working days after the current date in Europe/Amsterdam,
WHEN the obligation is retrieved,
THE SYSTEM SHALL return deadlineState=AT_RISK.

`AC-004`
WHEN an obligation is created with a due date that is not a working day,
THE SYSTEM SHALL set effectiveDueDate to the next working day.

`AC-005`
WHILE the effective due date of an obligation is more than three working days after the current date,
WHEN the obligation is retrieved,
THE SYSTEM SHALL return deadlineState=ON_TRACK.

`AC-006`
WHILE the effective due date of an obligation is before the current date,
WHEN the obligation is retrieved,
THE SYSTEM SHALL return deadlineState=OVERDUE.

`AC-007`
WHILE deadlineState is AT_RISK or OVERDUE,
THE SYSTEM SHALL return escalationState=ESCALATED and escalationOwner set to the owner email.
Otherwise, THE SYSTEM SHALL return escalationState=NONE.

`AC-008`
WHEN the due-soon report is requested,
THE SYSTEM SHALL return all AT_RISK and OVERDUE obligations, earliest effective due date first.

`AC-009`
IF an obligation is requested by an identifier that does not exist,
THEN THE SYSTEM SHALL return status 404 and a Problem Details body.

### Key Entities

- **Regulatory filing obligation**: A filing with an identifier, due date, owner email, effective due date, deadline state, and escalation state.
- **Working-day calendar**: The Europe/Amsterdam calendar rules and fixed demo holidays used to determine an effective due date and working-day intervals.
- **Due-soon report**: The ordered set of obligations whose deadline state is `AT_RISK` or `OVERDUE`.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- A valid filing request results in a stored obligation and a response with a server-assigned identifier and effective due date, as specified by `AC-001`.
- Obligations due zero through three working days from the current date are reported as `AT_RISK`; those more than three working days away are `ON_TRACK`; those in the past are `OVERDUE`, as specified by `AC-003`, `AC-005`, and `AC-006`.
- The due-soon report returns all `AT_RISK` and `OVERDUE` obligations in earliest-effective-due-date-first order, as specified by `AC-008`.

## Assumptions

- The demo uses a fixed list of public holidays for 2026 and 2027.
- Dates are evaluated in Europe/Amsterdam.
- The fixed demo holiday list is New Year's Day, Easter Monday, King's Day, Liberation Day, Ascension Day, Whit Monday, Christmas Day, and Boxing Day, for 2026 and 2027.
- Overdue obligations are part of the due-soon report because an overdue filing needs attention.
- API errors use RFC 9457 Problem Details (`application/problem+json`).

### Test Outline

| Criterion | Test setup | Expected observable result | Potential ambiguity |
|---|---|---|---|
| `AC-003` | Current date Monday 2026-10-05; due Thursday 2026-10-08 (three working days) | `effectiveDueDate=2026-10-08`, `AT_RISK`, `ESCALATED` | _(none)_ |
| `AC-005` | Current date Monday 2026-10-05; due Friday 2026-10-09 (four working days) | `ON_TRACK`, `NONE` | _(none)_ |
| `AC-004` | Due Saturday 2026-10-10 | `effectiveDueDate=2026-10-12` (Monday) | _(none)_ |
| `AC-004` | Due Sunday 2026-04-26; Monday 2026-04-27 is King's Day | `effectiveDueDate=2026-04-28` (Tuesday) | _(none)_ |
