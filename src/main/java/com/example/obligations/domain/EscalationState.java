package com.example.obligations.domain;

public enum EscalationState {
    NONE,
    ESCALATED,
    /** DEC-003: the escalation was seen; the deadline itself is unchanged. */
    ACKNOWLEDGED
}
