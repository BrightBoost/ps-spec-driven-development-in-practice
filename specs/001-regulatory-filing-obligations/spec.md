# Feature Specification: Regulatory filing obligations

Feature branch: `001-regulatory-filing-obligations`
Source: approved `.sdd/spec.md` (Module 1)

Status: approved for planning

## Scope

One regulatory filing obligation has one due date, one owner email, one compliance lead email, one effective due date, and is at risk from three working days before its effective due date.

| | |
|---|---|
| **In scope** | One obligation type: a regulatory filing obligation. Create an obligation with a due date, an owner email, and a compliance lead email. Calculate an effective due date: a due date on a weekend or a fixed demo holiday rolls forward to the next working day (Europe/Amsterdam). An obligation is at risk from three working days before its effective due date. A due-soon report of obligations that need attention. Escalation as a visible state on the obligation. |
| **Out of scope** | Recurring obligations. Automatic email delivery or any other notification. Multiple escalation levels. User-configurable calendars. A configurable at-risk threshold per obligation. Role-based administration. Undoing an acknowledgment (DEC-003). |
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

**Scenario 1: a filing that needs attention** (illustrates `AC-003`, `AC-007`
WHILE deadlineState is AT_RISK or OVERDUE and the obligation has not been acknowledged,
THE SYSTEM SHALL return escalationState=ESCALATED and escalationRecipients containing the owner email and the compliance lead email (DEC-002).
WHILE deadlineState is ON_TRACK, THE SYSTEM SHALL return escalationState=NONE and an empty escalationRecipients list.

`AC-008`
WHEN the due-soon report is requested,
THE SYSTEM SHALL return all AT_RISK and OVERDUE obligations, earliest effective due date first.
Acknowledged obligations remain in the report and show escalationState=ACKNOWLEDGED (DEC-003).

`AC-009`
IF an obligation is requested by an identifier that does not exist,
THEN THE SYSTEM SHALL return status 404 and a Problem Details body.

`AC-010`
WHEN the owner or compliance lead acknowledges an ESCALATED obligation,
THE SYSTEM SHALL set escalationState=ACKNOWLEDGED and leave deadlineState unchanged.
IF an obligation that is not ESCALATED is acknowledged,
THEN THE SYSTEM SHALL reject the request with status 409 and a Problem Details body.

> Rationale (DEC-003): acknowledgment records that an escalation was seen; it does not change the deadline. Acknowledged obligations stay in the due-soon report. Identity is not verified: authentication and role-based administration are out of scope.

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
