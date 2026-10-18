package com.example.obligations.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

import com.example.obligations.calendar.WorkingDayCalendar;

class DeadlineStatePolicyTest {

    @Test
    void ac003_marksAnEffectiveDueDateThreeWorkingDaysAwayAtRiskAcrossAWeekend() {
        DeadlineStatePolicy policy = policy("2026-10-09");

        assertThat(policy.deadlineState(LocalDate.of(2026, 10, 14)))
                .isEqualTo(DeadlineState.AT_RISK);
    }

    @Test
    void dueTodayIsAtRisk() {
        DeadlineStatePolicy policy = policy("2026-10-05");

        assertThat(policy.deadlineState(LocalDate.of(2026, 10, 5)))
                .isEqualTo(DeadlineState.AT_RISK);
    }

    @Test
    void ac005_marksAnEffectiveDueDateFourWorkingDaysAwayOnTrackAcrossAWeekend() {
        DeadlineStatePolicy policy = policy("2026-10-09");

        assertThat(policy.deadlineState(LocalDate.of(2026, 10, 15)))
                .isEqualTo(DeadlineState.ON_TRACK);
    }

    @Test
    void ac006_marksAnEffectiveDueDateBeforeTodayOverdue() {
        DeadlineStatePolicy policy = policy("2026-10-05");

        assertThat(policy.deadlineState(LocalDate.of(2026, 10, 2)))
                .isEqualTo(DeadlineState.OVERDUE);
    }

    @Test
    void ac004_rollsAWeekendDueDateForwardToTheNextWorkingDay() {
        DeadlineStatePolicy policy = policy("2026-10-05");

        assertThat(policy.effectiveDueDate(LocalDate.of(2026, 10, 10)))
                .isEqualTo(LocalDate.of(2026, 10, 12));
    }

    @Test
    void ac004_rollsPastAWeekendAndFixedHolidayToTheNextWorkingDay() {
        DeadlineStatePolicy policy = policy("2026-10-05");

        assertThat(policy.effectiveDueDate(LocalDate.of(2026, 4, 26)))
                .isEqualTo(LocalDate.of(2026, 4, 28));
    }

    @Test
    void usesTheAmsterdamDateEvenWhenTheClockUsesUtc() {
        Clock clock = Clock.fixed(Instant.parse("2026-10-04T22:30:00Z"), ZoneOffset.UTC);
        DeadlineStatePolicy policy = new DeadlineStatePolicy(new WorkingDayCalendar(), clock);

        assertThat(policy.deadlineState(LocalDate.of(2026, 10, 8)))
                .isEqualTo(DeadlineState.AT_RISK);
    }

    private static DeadlineStatePolicy policy(String today) {
        Clock clock = Clock.fixed(
                LocalDate.parse(today).atStartOfDay(ZoneOffset.UTC).toInstant(),
                ZoneOffset.UTC);
        return new DeadlineStatePolicy(new WorkingDayCalendar(), clock);
    }
}
