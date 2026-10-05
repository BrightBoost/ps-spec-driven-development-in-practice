package com.example.obligations.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;

import org.junit.jupiter.api.Test;

import com.example.obligations.calendar.WorkingDayCalendar;

/** Service-level evidence without starting the web layer. Today: Monday 2026-10-05. */
class ObligationServiceTest {

    private static final ZoneId AMSTERDAM = ZoneId.of("Europe/Amsterdam");

    private final ObligationService service = new ObligationService(
            new ObligationRepository(),
            new DeadlineStatePolicy(new WorkingDayCalendar()),
            Clock.fixed(LocalDate.of(2026, 10, 5).atTime(12, 0).atZone(AMSTERDAM).toInstant(), AMSTERDAM));

    @Test
    void ac001_createsWithEffectiveDueDate() {
        ObligationView created = service.create("2026-10-10", "owner@example.com", "lead@example.com");

        assertThat(created.obligation().id()).startsWith("obl-");
        assertThat(created.obligation().effectiveDueDate()).isEqualTo(LocalDate.of(2026, 10, 12));
    }

    @Test
    void ac002_rejectsInvalidDateAndBlankEmails() {
        assertThatThrownBy(() -> service.create("2026-13-45", "owner@example.com", "lead@example.com"))
                .isInstanceOf(InvalidObligationException.class);
        assertThatThrownBy(() -> service.create("2026-10-10", " ", "lead@example.com"))
                .isInstanceOf(InvalidObligationException.class);
        assertThatThrownBy(() -> service.create("2026-10-10", "owner@example.com", ""))
                .isInstanceOf(InvalidObligationException.class);
    }

    @Test
    void ac007_escalationFollowsDeadlineState() {
        ObligationView atRisk = service.create("2026-10-08", "owner@example.com", "lead@example.com");
        ObligationView onTrack = service.create("2026-10-09", "owner@example.com", "lead@example.com");

        assertThat(atRisk.escalationState()).isEqualTo(EscalationState.ESCALATED);
        assertThat(atRisk.escalationRecipients()).containsExactly("owner@example.com", "lead@example.com");
        assertThat(onTrack.escalationState()).isEqualTo(EscalationState.NONE);
        assertThat(onTrack.escalationRecipients()).isEmpty();
    }

    @Test
    void ac008_dueSoonListsAtRiskAndOverdueEarliestFirst() {
        String later = service.create("2026-10-08", "a@example.com", "lead@example.com").obligation().id();
        service.create("2026-10-20", "b@example.com", "lead@example.com");
        String overdue = service.create("2026-10-01", "c@example.com", "lead@example.com").obligation().id();
        String sooner = service.create("2026-10-06", "d@example.com", "lead@example.com").obligation().id();

        assertThat(service.dueSoon()).extracting(view -> view.obligation().id())
                .containsExactly(overdue, sooner, later);
    }

    @Test
    void ac009_unknownIdentifierIsNotFound() {
        assertThatThrownBy(() -> service.get("obl-999")).isInstanceOf(ObligationNotFoundException.class);
    }
}
