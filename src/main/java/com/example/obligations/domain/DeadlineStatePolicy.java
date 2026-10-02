package com.example.obligations.domain;

import java.time.LocalDate;

import com.example.obligations.calendar.WorkingDayCalendar;

/**
 * Deadline state from the effective due date and today (AC-003, AC-005, AC-006).
 * The effective due date itself comes from the calendar (AC-004).
 */
public class DeadlineStatePolicy {

    /** AC-003: at risk from three working days before the effective due date. */
    static final int AT_RISK_WORKING_DAYS = 3;

    private final WorkingDayCalendar calendar;

    public DeadlineStatePolicy(WorkingDayCalendar calendar) {
        this.calendar = calendar;
    }

    public LocalDate effectiveDueDate(LocalDate dueDate) {
        return calendar.onOrNextWorkingDay(dueDate);
    }

    public DeadlineState stateFor(LocalDate effectiveDueDate, LocalDate today) {
        if (effectiveDueDate.isBefore(today)) {
            return DeadlineState.OVERDUE;
        }
        int workingDaysLeft = calendar.workingDaysBetween(today, effectiveDueDate);
        return workingDaysLeft <= AT_RISK_WORKING_DAYS ? DeadlineState.AT_RISK : DeadlineState.ON_TRACK;
    }
}
