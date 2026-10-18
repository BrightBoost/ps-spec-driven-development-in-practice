package com.example.obligations.domain;

import java.util.List;

/** An obligation together with the state calculated for today. */
public record ObligationView(
        Obligation obligation,
        DeadlineState deadlineState,
        EscalationState escalationState,
        List<String> escalationRecipients) {
}
