package com.example.obligations.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.time.LocalDate;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

class ObligationRepositoryTest {

    @TempDir
    Path dir;

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
    void obligationsSurviveARestartAndIdentifiersKeepCounting() {
        DataSource file = new DriverManagerDataSource("jdbc:h2:file:" + dir.resolve("restart"));
        LocalDate due = LocalDate.of(2026, 10, 8);
        Obligation stored = new ObligationRepository(file).add(due, due, "owner@example.com", "lead@example.com");
        new ObligationRepository(file).acknowledge(stored.id());

        ObligationRepository afterRestart = new ObligationRepository(file);

        assertThat(afterRestart.findById(stored.id())).contains(
                new Obligation(stored.id(), due, due, "owner@example.com", "lead@example.com", true));
        assertThat(afterRestart.add(due, due, "owner@example.com", "lead@example.com").id())
                .isNotEqualTo(stored.id());
    }

    @Test
    void returnsEmptyForUnknownIdentifier() {
        assertThat(repository.findById("obl-999")).isEmpty();
    }
}
