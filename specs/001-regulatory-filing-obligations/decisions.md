# Decisions: Regulatory filing obligations

## DEC-001: Approve plan after adding AC-003 boundary tests and AC-009 controller test

- Date: 2026-09-30
- Context: The draft plan had no test at the at-risk boundary (three and four working days) and no controller test for an unknown identifier.
- Decision: Add both to the test strategy and add a traceability table to the plan.
- Approved by: Maaike and the viewer, as developer reviewers
- Status: Approved for tasking

## DEC-002: Show owner and compliance lead as escalation recipients

- Date: 2026-10-01
- Rationale: The compliance lead must see every escalated filing, not only the owner.
- Change: `escalationOwner` becomes `escalationRecipients`; create requests include a compliance lead email.
- Affected IDs: FR-001, EC-008, AC-001, AC-002, AC-007; plan (escalation, data flow); tasks T-002, T-004, T-005 (obsolete), T-010 (new)
- Out of scope: showing recipients in the browser view (not required by the spec)
- Approved by: the lawyer, as product owner
- Plan: re-approved by Maaike and the viewer, as developer reviewers

## DEC-003: Add ACKNOWLEDGED as an escalation state

- Date: 2026-10-26
- Decision: An escalated obligation can be acknowledged by its owner or compliance lead. Its escalation state becomes `ACKNOWLEDGED`; its deadline state does not change.
- Rationale: A reviewed escalation must be dismissible without changing or hiding the deadline itself.
- Alternatives rejected: Deleting the escalation; removing the obligation from the due-soon report (an acknowledged filing is still due, and the lawyer still wants to see it); a separate hidden flag.
- Excluded: Undoing an acknowledgment (product decision by the lawyer, 2026-10-26).
- Affected IDs: FR-008 (new), AC-007, AC-008, AC-010 (new); plan (API, escalation, data flow); task T-011 (new)
- Approved by: the lawyer, as product owner
- Plan: re-approved by Maaike and the viewer, as developer reviewers
