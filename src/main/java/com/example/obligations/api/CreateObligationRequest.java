package com.example.obligations.api;

/** The due date is a string so an invalid date becomes a clear 400 (AC-002), not a parse error. */
public record CreateObligationRequest(String dueDate, String ownerEmail, String complianceLeadEmail) {
}
