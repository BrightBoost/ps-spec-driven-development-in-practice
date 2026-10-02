package com.example.obligations.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class ObligationRepositoryTest {

    private final ObligationRepository repository = new ObligationRepository();

    @Test
    void assignsUniqueServerSideIdentifiers() {
        LocalDate due = LocalDate.of(2026, 10, 8);
        Obligation first = repository.add(due, due, "owner@example.com", "lead@example.com");
        Obligation second = repository.add(due, due, "owner@example.com", "lead@example.com");

        assertThat(first.id()).startsWith("obl-");
        assertThat(second.id()).isNotEqualTo(first.id());
    }

    @Test
    void findsAnObligationByItsIdentifier() {
        LocalDate due = LocalDate.of(2026, 10, 8);
        Obligation stored = repository.add(due, due, "owner@example.com", "lead@example.com");

        assertThat(repository.findById(stored.id())).contains(stored);
    }

    @Test
    void returnsEmptyForUnknownIdentifier() {
        assertThat(repository.findById("obl-999")).isEmpty();
    }
}
