# Obligations Tracker: regulatory filing obligations

Status: draft

## Scope

One regulatory filing obligation has one due date, one owner email, one effective due date, and is at risk from three working days before its effective due date.

| | |
|---|---|
| **In scope** | One obligation type: a regulatory filing obligation. Create an obligation with a due date and an owner email. Calculate an effective due date: a due date on a weekend or a fixed demo holiday rolls forward to the next working day (Europe/Amsterdam). An obligation is at risk from three working days before its effective due date. A due-soon report of obligations that need attention. Escalation as a visible state on the obligation. |
| **Out of scope** | Recurring obligations. Automatic email delivery or any other notification. Multiple escalation levels. User-configurable calendars. A configurable at-risk threshold per obligation. Role-based administration. |
| **Actors** | A lawyer on the compliance team (product owner and main user). Developers maintaining the tracker. |
| **Inputs** | Due date. Owner email. |
| **Outputs** | Obligation with identifier, due date, effective due date, deadline state, and escalation state. Due-soon report. |
| **Assumptions** | The demo uses a fixed list of public holidays for 2026 and 2027. Dates are evaluated in Europe/Amsterdam. |
| **Open questions** | _(none: see answers below)_ |

### Answered questions

- Which holidays are on the fixed demo list? New Year's Day, Easter Monday, King's Day, Liberation Day, Ascension Day, Whit Monday, Christmas Day, and Boxing Day, for 2026 and 2027.
- Are overdue obligations part of the due-soon report? Yes. An overdue filing needs attention.
- Which error format does the API use? RFC 9457 Problem Details (`application/problem+json`).

## Functional requirements

- `FR-001`: The system shall create a regulatory filing obligation from a due date and an owner email
- `FR-002`: The system shall retrieve an obligation by its server-assigned identifier
- `FR-003`: The system shall list obligations that are at risk or overdue, sorted by effective due date earliest first
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
- `EC-008`: The owner email is blank
- `EC-009`: The obligation identifier does not exist

## Non-functional requirements

- `NFR-001`: The API accepts and returns dates in ISO-8601 format
- `NFR-002`: Dates are evaluated in Europe/Amsterdam, and the current date can be controlled in tests
- `NFR-003`: The working-day calendar uses a fixed list of demo holidays for 2026 and 2027
- `NFR-004`: Errors use RFC 9457 Problem Details (`application/problem+json`)
- `NFR-005`: All tests run through the Maven wrapper
