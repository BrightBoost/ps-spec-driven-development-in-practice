package com.example.obligations.domain;

public class ObligationNotFoundException extends RuntimeException {

    public ObligationNotFoundException(String id) {
        super("No obligation with identifier " + id);
    }
}
