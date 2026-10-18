package com.example.obligations.domain;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;

import com.example.obligations.calendar.WorkingDayCalendar;

public class DeadlineStatePolicy {

    private static final int AT_RISK_WORKING_DAYS = 3;
    private static final ZoneId BUSINESS_ZONE = ZoneId.of("Europe/Amsterdam");

    private final WorkingDayCalendar calendar;
    private final Clock clock;

    public DeadlineStatePolicy(WorkingDayCalendar calendar, Clock clock) {
        this.calendar = calendar;
        this.clock = clock;
    }

    public LocalDate effectiveDueDate(LocalDate dueDate) {
        return calendar.onOrNextWorkingDay(dueDate);
    }

    public DeadlineState deadlineState(LocalDate effectiveDueDate) {
        LocalDate today = LocalDate.now(clock.withZone(BUSINESS_ZONE));
        if (effectiveDueDate.isBefore(today)) {
            return DeadlineState.OVERDUE;
        }
        if (calendar.workingDaysBetween(today, effectiveDueDate) <= AT_RISK_WORKING_DAYS) {
            return DeadlineState.AT_RISK;
        }
        return DeadlineState.ON_TRACK;
    }
}
