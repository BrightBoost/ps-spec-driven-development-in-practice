package com.example.obligations;

import org.junit.jupiter.api.Test;

/**
 * Test outline derived from the acceptance criteria in .sdd/spec.md.
 * There is no application yet: each method names the criterion it will prove.
 */
class ObligationDeadlineControllerTest {

    @Test
    void ac001_createsObligationWithServerAssignedIdAndEffectiveDueDate() {
        // TODO arrange a valid due date and owner email, POST, assert 201, id, effectiveDueDate
    }

    @Test
    void ac002_rejectsInvalidDueDateOrBlankOwnerWithProblemDetails() {
        // TODO POST an invalid date and a blank owner, assert 400 and application/problem+json
    }

    @Test
    void ac003_isAtRiskAtExactlyThreeWorkingDays() {
        // TODO current date Monday, due Thursday, assert AT_RISK and ESCALATED
    }

    @Test
    void ac005_isOnTrackAtFourWorkingDays() {
        // TODO current date Monday, due Friday, assert ON_TRACK and NONE
    }

    @Test
    void ac004_rollsWeekendDueDateForwardToNextWorkingDay() {
        // TODO due Saturday, assert effectiveDueDate is the following Monday
    }

    @Test
    void ac006_isOverdueWhenEffectiveDueDateIsInThePast() {
        // TODO due date before the current date, assert OVERDUE
    }

    @Test
    void ac007_escalationOwnerIsReturnedWhenAtRisk() {
        // TODO assert escalationState=ESCALATED and escalationOwner
    }

    @Test
    void ac009_returnsNotFoundForUnknownIdentifier() {
        // TODO GET an unknown id, assert 404 and application/problem+json
    }
}
