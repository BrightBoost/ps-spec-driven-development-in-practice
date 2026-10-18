package com.example.obligations.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.example.obligations.calendar.WorkingDayCalendar;

class DeadlineStatePolicyTest {

    private final DeadlineStatePolicy policy = new DeadlineStatePolicy(new WorkingDayCalendar());

    /** Boundary table from the spec; dates are Europe/Amsterdam calendar dates. */
    @ParameterizedTest(name = "[{3}] today {0}, due {1} -> {2}")
    @CsvSource({
            "2026-10-05, 2026-10-09, ON_TRACK, AC-005 four working days",
            "2026-10-05, 2026-10-08, AT_RISK,  AC-003 three working days",
            "2026-10-05, 2026-10-07, AT_RISK,  AC-003 two working days",
            "2026-10-05, 2026-10-06, AT_RISK,  AC-003 one working day",
            "2026-10-05, 2026-10-05, AT_RISK,  AC-003 due today",
            "2026-10-05, 2026-10-02, OVERDUE,  AC-006 due before today",
            "2026-10-09, 2026-10-14, AT_RISK,  AC-003 three working days across a weekend",
            "2026-04-22, 2026-04-28, AT_RISK,  AC-003 three working days excluding King's Day"
    })
    void deadlineStateFollowsTheBoundaryTable(LocalDate today, LocalDate effectiveDueDate,
                                               DeadlineState expected, String criterion) {
        assertThat(policy.stateFor(effectiveDueDate, today)).isEqualTo(expected);
    }

    @Test
    void ac004_weekendDueDateRollsForwardToMonday() {
        assertThat(policy.effectiveDueDate(LocalDate.of(2026, 10, 10))).isEqualTo(LocalDate.of(2026, 10, 12));
    }

    @Test
    void ac004_holidayMondayPushesTheEffectiveDueDateToTuesday() {
        assertThat(policy.effectiveDueDate(LocalDate.of(2026, 4, 26))).isEqualTo(LocalDate.of(2026, 4, 28));
    }
}
