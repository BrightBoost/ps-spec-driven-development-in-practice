# Feature Specification: Regulatory filing obligations

Feature branch: `001-regulatory-filing-obligations`
Source: approved `.sdd/spec.md` (Module 1)

Status: approved for planning

## Scope

One regulatory filing obligation has one due date, one owner email, one compliance lead email, one effective due date, and is at risk from three working days before its effective due date.

| | |
|---|---|
| **In scope** | One obligation type: a regulatory filing obligation. Create an obligation with a due date, an owner email, and a compliance lead email. Calculate an effective due date: a due date on a weekend or a fixed demo holiday rolls forward to the next working day (Europe/Amsterdam). An obligation is at risk from three working days before its effective due date. A due-soon report of obligations that need attention. Escalation as a visible state on the obligation. |
| **Out of scope** | Recurring obligations. Automatic email delivery or any other notification. Multiple escalation levels. User-configurable calendars. A configurable at-risk threshold per obligation. Role-based administration. |
| **Actors** | A lawyer on the compliance team (product owner and main user). Developers maintaining the tracker. |
| **Inputs** | Due date. Owner email. Compliance lead email (DEC-002). |
| **Outputs** | Obligation with identifier, due date, effective due date, deadline state, and escalation state. Due-soon report. |
| **Assumptions** | The demo uses a fixed list of public holidays for 2026 and 2027. Dates are evaluated in Europe/Amsterdam. |
| **Open questions** | _(none: see answers below)_ |

### Answered questions

- Which holidays are on the fixed demo list? New Year's Day, Easter Monday, King's Day, Liberation Day, Ascension Day, Whit Monday, Christmas Day, and Boxing Day, for 2026 and 2027.
- Are overdue obligations part of the due-soon report? Yes. An overdue filing needs attention.
- Which error format does the API use? RFC 9457 Problem Details (`application/problem+json`).

## User scenarios (illustrative)

The EARS acceptance criteria below are authoritative. These scenarios illustrate them.

**Scenario 1: a filing that needs attention** (illustrates `AC-003`, `AC-007`)
- Given today is Monday 2026-10-05
- And a filing obligation is due on Thursday 2026-10-08
- When the lawyer retrieves the obligation
- Then its deadline state is `AT_RISK`, its escalation state is `ESCALATED`, and the owner and compliance lead are listed as escalation recipients

**Scenario 2: a due date on a weekend** (illustrates `AC-004`)
- Given a filing obligation is created with a due date of Saturday 2026-10-10
- Then its effective due date is Monday 2026-10-12

**Scenario 3: the due-soon report** (illustrates `AC-008`)
- Given one overdue obligation and two at-risk obligations
- When the lawyer opens the due-soon report
- Then all three are listed, earliest effective due date first

## Functional requirements

- `FR-001`: The system shall create a regulatory filing obligation from a due date, an owner email, and a compliance lead email (DEC-002)
- `FR-002`: The system shall retrieve an obligation by its server-assigned identifier
- `FR-003`: The system shall list obligations that are at risk or overdue, sorted by effective due date, earliest first
- `FR-004`: The system shall calculate an effective due date using Europe/Amsterdam working-day rules
- `FR-005`: The system shall expose a deadline state of `ON_TRACK`, `AT_RISK`, or `OVERDUE`
- `FR-006`: The system shall expose escalation as a visible state and shall not send notifications
- `FR-007`: The system shall reject an unknown obligation identifier with a not-found error

## Edge cases

- `EC-001`: The due date is today
- `EC-002`: The effective due date is exactly three working days away
- `EC-003`: The effective due date is four working days away
- `EC-004`: The due date falls on a weekend
- `EC-005`: The due date falls on a fixed demo holiday
- `EC-006`: The effective due date is in the past
- `EC-007`: The due date is not a valid date
- `EC-008`: The owner email or the compliance lead email is blank
- `EC-009`: The obligation identifier does not exist

## Non-functional requirements

- `NFR-001`: The API accepts and returns dates in ISO-8601 format
- `NFR-002`: Dates are evaluated in Europe/Amsterdam, and the current date can be controlled in tests
- `NFR-003`: The working-day calendar uses a fixed list of demo holidays for 2026 and 2027
- `NFR-004`: Errors use RFC 9457 Problem Details (`application/problem+json`)
- `NFR-005`: All tests run through the Maven wrapper

## Acceptance criteria

`AC-001`
WHEN a create request contains a valid due date, owner email, and compliance lead email,
THE SYSTEM SHALL store the obligation and return status 201 with a server-assigned identifier and the effective due date.

`AC-002`
IF a create request contains an invalid due date, a blank owner email, or a blank compliance lead email,
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
THE SYSTEM SHALL return escalationState=ESCALATED and escalationRecipients containing the owner email and the compliance lead email (DEC-002).
Otherwise, THE SYSTEM SHALL return escalationState=NONE and an empty escalationRecipients list.

`AC-008`
WHEN the due-soon report is requested,
THE SYSTEM SHALL return all AT_RISK and OVERDUE obligations, earliest effective due date first.

`AC-009`
IF an obligation is requested by an identifier that does not exist,
THEN THE SYSTEM SHALL return status 404 and a Problem Details body.

## Clarifications

### Session 2026-09-30

- Q: Which date counts as the current date for a request that arrives just after midnight? A: The local date in Europe/Amsterdam (see constitution).

## Test outline

| Criterion | Test setup | Expected observable result | Potential ambiguity |
|---|---|---|---|
| `AC-003` | Current date Monday 2026-10-05; due Thursday 2026-10-08 (three working days) | `effectiveDueDate=2026-10-08`, `AT_RISK`, `ESCALATED` | _(none)_ |
| `AC-005` | Current date Monday 2026-10-05; due Friday 2026-10-09 (four working days) | `ON_TRACK`, `NONE` | _(none)_ |
| `AC-004` | Due Saturday 2026-10-10 | `effectiveDueDate=2026-10-12` (Monday) | _(none)_ |
| `AC-004` | Due Sunday 2026-04-26; Monday 2026-04-27 is King's Day | `effectiveDueDate=2026-04-28` (Tuesday) | _(none)_ |
