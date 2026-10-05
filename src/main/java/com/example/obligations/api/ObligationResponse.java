package com.example.obligations.api;

import java.util.List;

import com.example.obligations.domain.ObligationView;

/** Dates are ISO-8601 strings on the wire (NFR-001), independent of JSON library defaults. */
public record ObligationResponse(
        String id,
        String dueDate,
        String effectiveDueDate,
        String deadlineState,
        String escalationState,
        List<String> escalationRecipients) {

    static ObligationResponse from(ObligationView view) {
        return new ObligationResponse(
                view.obligation().id(),
                view.obligation().dueDate().toString(),
                view.obligation().effectiveDueDate().toString(),
                view.deadlineState().name(),
                view.escalationState().name(),
                view.escalationRecipients());
    }
}
