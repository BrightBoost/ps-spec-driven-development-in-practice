package com.example.obligations.domain;

import java.time.LocalDate;

import com.example.obligations.calendar.WorkingDayCalendar;

/**
 * Deadline state from the effective due date and today (AC-003, AC-005, AC-006).
 * The effective due date itself comes from the calendar (AC-004).
 */
public class DeadlineStatePolicy {

    private final WorkingDayCalendar calendar;

    public DeadlineStatePolicy(WorkingDayCalendar calendar) {
        this.calendar = calendar;
    }

    public LocalDate effectiveDueDate(LocalDate dueDate) {
        return calendar.onOrNextWorkingDay(dueDate);
    }

    /** Determines state using the current Europe/Amsterdam date and working days. */
    public DeadlineState stateFor(LocalDate effectiveDueDate, LocalDate today) {
        if (effectiveDueDate.isBefore(today)) {
            return DeadlineState.OVERDUE;
        }
        return calendar.workingDaysBetween(today, effectiveDueDate) <= 3
                ? DeadlineState.AT_RISK
                : DeadlineState.ON_TRACK;
    }
}
