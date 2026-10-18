package com.example.obligations.domain;

public class InvalidObligationException extends RuntimeException {

    private final String field;

    public InvalidObligationException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String field() {
        return field;
    }
}
