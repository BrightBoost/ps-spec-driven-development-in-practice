package com.example.obligations.domain;

import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;

public class ObligationService {

    private final ObligationRepository repository;
    private final DeadlineStatePolicy policy;
    private final Clock clock;

    public ObligationService(ObligationRepository repository, DeadlineStatePolicy policy, Clock clock) {
        this.repository = repository;
        this.policy = policy;
        this.clock = clock;
    }

    /** AC-001, AC-002, AC-004 */
    public ObligationView create(String dueDate, String ownerEmail, String complianceLeadEmail) {
        LocalDate parsedDueDate = parseDueDate(dueDate);
        requireNotBlank("ownerEmail", ownerEmail);
        requireNotBlank("complianceLeadEmail", complianceLeadEmail);
        LocalDate effectiveDueDate = policy.effectiveDueDate(parsedDueDate);
        Obligation obligation = repository.add(parsedDueDate, effectiveDueDate, ownerEmail, complianceLeadEmail);
        return view(obligation, today());
    }

    /** AC-003, AC-005, AC-006, AC-007, AC-009 */
    public ObligationView get(String id) {
        Obligation obligation = repository.findById(id).orElseThrow(() -> new ObligationNotFoundException(id));
        return view(obligation, today());
    }

    /** AC-010 */
    public ObligationView acknowledge(String id) {
        LocalDate today = today();
        Obligation obligation = repository.findById(id).orElseThrow(() -> new ObligationNotFoundException(id));
        if (obligation.acknowledged()
                || policy.stateFor(obligation.effectiveDueDate(), today) == DeadlineState.ON_TRACK
                || !repository.acknowledge(id)) {
            throw new ObligationAcknowledgementConflictException(id);
        }
        Obligation acknowledged = repository.findById(id).orElseThrow(() -> new ObligationNotFoundException(id));
        return view(acknowledged, today);
    }

    /** AC-008: at-risk and overdue obligations, earliest effective due date first. */
    public List<ObligationView> dueSoon() {
        LocalDate today = today();
        return repository.findAll().stream()
                .map(obligation -> view(obligation, today))
                .filter(view -> view.deadlineState() != DeadlineState.ON_TRACK)
                .sorted(Comparator
                        .comparing((ObligationView view) -> view.obligation().effectiveDueDate())
                        .thenComparing(view -> view.obligation().id()))
                .toList();
    }

    private ObligationView view(Obligation obligation, LocalDate today) {
        DeadlineState deadlineState = policy.stateFor(obligation.effectiveDueDate(), today);
        if (obligation.acknowledged()) {
            return new ObligationView(obligation, deadlineState, EscalationState.ACKNOWLEDGED, List.of());
        }
        if (deadlineState == DeadlineState.ON_TRACK) {
            return new ObligationView(obligation, deadlineState, EscalationState.NONE, List.of());
        }
        return new ObligationView(obligation, deadlineState, EscalationState.ESCALATED,
                List.of(obligation.ownerEmail(), obligation.complianceLeadEmail()));
    }

    private LocalDate today() {
        return LocalDate.now(clock);
    }

    private static LocalDate parseDueDate(String dueDate) {
        if (dueDate == null || dueDate.isBlank()) {
            throw new InvalidObligationException("dueDate", "dueDate is required");
        }
        try {
            return LocalDate.parse(dueDate);
        } catch (DateTimeParseException e) {
            throw new InvalidObligationException("dueDate", "dueDate must be an ISO-8601 date (yyyy-MM-dd)");
        }
    }

    private static void requireNotBlank(String field, String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidObligationException(field, field + " must not be blank");
        }
    }
}
