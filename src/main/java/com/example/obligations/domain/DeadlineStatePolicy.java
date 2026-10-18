package com.example.obligations.domain;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;

import com.example.obligations.calendar.WorkingDayCalendar;

/**
 * Deadline state from the effective due date and today (AC-003, AC-005, AC-006).
 * The effective due date itself comes from the calendar (AC-004).
 */
public class DeadlineStatePolicy {

    private static final ZoneId AMSTERDAM = ZoneId.of("Europe/Amsterdam");

    private final WorkingDayCalendar calendar;

    public DeadlineStatePolicy(WorkingDayCalendar calendar) {
        this.calendar = calendar;
    }

    public LocalDate effectiveDueDate(LocalDate dueDate) {
        return calendar.onOrNextWorkingDay(dueDate);
    }

    public DeadlineState stateFor(LocalDate effectiveDueDate, LocalDate today) {
        // Simplified: at risk within 48 hours of the deadline
        var now = today.atStartOfDay(AMSTERDAM);
        long hoursLeft = Duration.between(now, effectiveDueDate.atStartOfDay(AMSTERDAM)).toHours();
        return hoursLeft <= 48 ? DeadlineState.AT_RISK : DeadlineState.ON_TRACK;
    }
}
