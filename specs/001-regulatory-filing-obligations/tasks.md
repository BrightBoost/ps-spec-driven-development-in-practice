# Tasks: Regulatory filing obligations

Statuses: `Todo` | `In progress` | `Blocked` | `Done` (the agent reports completion) | `Verified` (linked evidence) | `Obsolete`

## T-001 Establish project principles and the fixed demo holiday calendar
- Goal: A working-day calendar with weekends and the fixed demo holidays for 2026 and 2027; a `Clock` in Europe/Amsterdam
- Spec links: NFR-002, NFR-003
- Depends on: -
- Files likely affected: `calendar/WorkingDayCalendar.java`, `config/ClockConfig.java`, `WorkingDayCalendarTest.java`
- Verification: `./mvnw -q -Dtest=WorkingDayCalendarTest test`
- Status: Verified
- Evidence: `./mvnw -q -Dtest=WorkingDayCalendarTest test` passed (9 tests), 2026-10-02

## T-002 Define the obligation representation and server-assigned identifier
- Goal: An obligation record (due date, owner email, compliance lead email) and an in-memory repository that assigns identifiers
- Spec links: FR-001, FR-002, DEC-002
- Depends on: T-001
- Files likely affected: `domain/Obligation.java`, `domain/ObligationRepository.java`, `ObligationRepositoryTest.java`
- Verification: `./mvnw -q -Dtest=ObligationRepositoryTest test`
- Status: Verified
- Evidence: `./mvnw -q -Dtest=ObligationRepositoryTest test` passed (3 tests), 2026-10-02

## T-003 Implement DeadlineStatePolicy
- Goal: Deadline state from the effective due date and today
- Spec links: AC-003, AC-004, AC-005, AC-006
- Depends on: T-001, T-002
- Files likely affected: `domain/DeadlineStatePolicy.java`, `domain/DeadlineState.java`, `DeadlineStatePolicyTest.java`
- Verification: `./mvnw -q -Dtest=DeadlineStatePolicyTest test`
- Status: Done

## T-004 Create and retrieve endpoints with Problem Details errors
- Goal: `POST /obligations` (including compliance lead email) and `GET /obligations/{id}`, with 400 and 404 as Problem Details
- Spec links: AC-001, AC-002, AC-009
- Depends on: T-002
- Files likely affected: `api/ObligationController.java`, `api/*Request.java`, `api/*Response.java`, `api/ApiExceptionHandler.java`, `domain/ObligationService.java`
- Verification: `./mvnw -q -Dtest=ObligationDeadlineControllerTest test`
- Status: Todo

## T-005 Expose escalation state and escalation owner
- Replaced by: T-010 (DEC-002)
- Goal: Escalation state and escalation owner in the obligation response
- Spec links: AC-007
- Depends on: T-003, T-004
- Files likely affected: `domain/EscalationState.java`, `api/ObligationResponse.java`
- Verification: `./mvnw -q -Dtest=ObligationDeadlineControllerTest test`
- Status: Obsolete

## T-006 Due-soon report endpoint and query
- Goal: `GET /obligations/due-soon` returning AT_RISK and OVERDUE obligations, earliest effective due date first
- Spec links: FR-003, AC-008
- Depends on: T-003, T-010
- Files likely affected: `domain/ObligationService.java`, `api/ObligationController.java`, `DueSoonReportTest.java`
- Verification: `./mvnw -q -Dtest=DueSoonReportTest test`
- Status: Todo

## T-007 Controller tests for invalid due date and unknown identifier
- Goal: Evidence for the error paths
- Spec links: EC-007, EC-009, AC-002, AC-009
- Depends on: T-004
- Files likely affected: `ObligationDeadlineControllerTest.java`
- Verification: `./mvnw -q -Dtest=ObligationDeadlineControllerTest test`
- Status: Todo

## T-008 Minimal browser view for the due-soon report
- Goal: A static page that shows the due-soon report
- Spec links: AC-008
- Depends on: T-006
- Files likely affected: `src/main/resources/static/index.html`
- Verification: open `http://localhost:8080/` with the demo profile and compare with `GET /obligations/due-soon`
- Status: Todo

## T-009 Full verification pass and traceability update
- Goal: Every acceptance criterion has passing evidence in the plan's traceability table
- Spec links: all AC-IDs
- Depends on: T-001 to T-008
- Verification: `./mvnw test`, independent audit, manual requests
- Status: Todo

## T-010 Expose escalation state and escalation recipients
- Goal: Escalation state and escalation recipients (owner and compliance lead) in the obligation response
- Spec links: AC-007, DEC-002
- Depends on: T-003, T-004
- Files likely affected: `domain/EscalationState.java`, `domain/EscalationPolicy.java`, `api/ObligationResponse.java`
- Verification: `./mvnw -q -Dtest=ObligationDeadlineControllerTest test`
- Status: Todo
