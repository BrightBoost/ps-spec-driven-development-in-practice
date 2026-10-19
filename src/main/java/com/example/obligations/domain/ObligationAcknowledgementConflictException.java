package com.example.obligations.domain;

public class ObligationAcknowledgementConflictException extends RuntimeException {

    public ObligationAcknowledgementConflictException(String id) {
        super("Obligation " + id + " is not currently escalated");
    }
}
