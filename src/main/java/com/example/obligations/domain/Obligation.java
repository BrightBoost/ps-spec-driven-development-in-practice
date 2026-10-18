package com.example.obligations.domain;

import java.time.LocalDate;

/**
 * A regulatory filing obligation. The effective due date is calculated once, at creation.
 * Deadline and escalation state are calculated on every read, because they depend on today.
 */
public record Obligation(
        String id,
        LocalDate dueDate,
        LocalDate effectiveDueDate,
        String ownerEmail,
        String complianceLeadEmail) {
}
