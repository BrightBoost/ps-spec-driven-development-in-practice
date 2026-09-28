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
