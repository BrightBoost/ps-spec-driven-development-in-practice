# Decisions: Regulatory filing obligations

## DEC-001: Approve plan after adding AC-003 boundary tests

- Date: 2026-10-18
- Context: The draft plan had no test at the at-risk boundary (three and four working days). Clarify also changed FR-001 to require persistence across restarts, which led to file-backed H2 storage.
- Decision: Add boundary tests and a traceability table to the plan. The restart-persistence requirement is accepted as a spec change.
- Approved by: Maaike and the viewer, as developer reviewers
- Status: Approved for tasking


## DEC-002: Show owner and compliance lead as escalation recipients

- Date: 2026-10-18
- Rationale: The compliance lead must see every escalated filing, not only the owner.
- Change: `escalationOwner` becomes `escalationRecipients`; create requests include a compliance lead email.
- Affected IDs: FR-001, EC-008, EC-010, AC-001, AC-002, AC-007; plan (escalation, data flow); tasks T-002, T-004, T-005 (obsolete), T-010 (new)
- Clarified response contract: AT_RISK and OVERDUE return `ESCALATED` with both emails in `escalationRecipients`; ON_TRACK returns `NONE` and omits `escalationRecipients`.
- Out of scope: showing recipients in the browser view (not required by the spec)
- Approved by: the lawyer, as product owner
- Plan: re-approved by Maaike and the viewer, as developer reviewers