package com.example.obligations.calendar;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class WorkingDayCalendarTest {

    private final WorkingDayCalendar calendar = new WorkingDayCalendar();

    @Test
    void weekdaysAreWorkingDays() {
        assertThat(calendar.isWorkingDay(LocalDate.of(2026, 10, 5))).isTrue(); // Monday
        assertThat(calendar.isWorkingDay(LocalDate.of(2026, 10, 9))).isTrue(); // Friday
    }

    @Test
    void weekendsAreNotWorkingDays() {
        assertThat(calendar.isWorkingDay(LocalDate.of(2026, 10, 10))).isFalse(); // Saturday
        assertThat(calendar.isWorkingDay(LocalDate.of(2026, 10, 11))).isFalse(); // Sunday
    }

    @Test
    void fixedDemoHolidaysAreNotWorkingDays() {
        assertThat(calendar.isWorkingDay(LocalDate.of(2026, 4, 27))).isFalse(); // King's Day, Monday
        assertThat(calendar.isWorkingDay(LocalDate.of(2026, 5, 14))).isFalse(); // Ascension Day, Thursday
    }

    @Test
    void saturdayRollsForwardToMonday() {
        assertThat(calendar.onOrNextWorkingDay(LocalDate.of(2026, 10, 10)))
                .isEqualTo(LocalDate.of(2026, 10, 12));
    }

    @Test
    void sundayBeforeAHolidayMondayRollsForwardToTuesday() {
        assertThat(calendar.onOrNextWorkingDay(LocalDate.of(2026, 4, 26)))
                .isEqualTo(LocalDate.of(2026, 4, 28));
    }

    @Test
    void holidayRollsForwardToNextWorkingDay() {
        assertThat(calendar.onOrNextWorkingDay(LocalDate.of(2026, 5, 14)))
                .isEqualTo(LocalDate.of(2026, 5, 15));
    }

    @Test
    void workingDayStaysTheSame() {
        assertThat(calendar.onOrNextWorkingDay(LocalDate.of(2026, 10, 8)))
                .isEqualTo(LocalDate.of(2026, 10, 8));
    }

    @Test
    void countsWorkingDaysAfterFromUpToAndIncludingTo() {
        LocalDate monday = LocalDate.of(2026, 10, 5);
        assertThat(calendar.workingDaysBetween(monday, monday)).isZero();
        assertThat(calendar.workingDaysBetween(monday, LocalDate.of(2026, 10, 8))).isEqualTo(3);
        assertThat(calendar.workingDaysBetween(monday, LocalDate.of(2026, 10, 9))).isEqualTo(4);
    }

    @Test
    void workingDayCountSkipsTheWeekend() {
        LocalDate friday = LocalDate.of(2026, 10, 9);
        assertThat(calendar.workingDaysBetween(friday, LocalDate.of(2026, 10, 14))).isEqualTo(3);
    }
}
