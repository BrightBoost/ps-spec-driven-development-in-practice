package com.example.obligations.domain;

public class AcknowledgementNotAllowedException extends RuntimeException {

    public AcknowledgementNotAllowedException(String id) {
        super("Obligation " + id + " is not escalated and cannot be acknowledged");
    }
}
