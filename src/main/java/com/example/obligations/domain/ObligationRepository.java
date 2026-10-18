package com.example.obligations.domain;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 * JDBC storage for the approved scope; the application uses a file-backed H2 database so
 * obligations survive restarts (FR-001). Identifiers are server-assigned (FR-002),
 * so clients can never send a duplicate identifier.
 */
public class ObligationRepository {

    private static final int FIRST_SEQUENCE = 100;

    private final JdbcTemplate jdbc;

    /** Private in-memory database, for tests that do not need a Spring context. */
    public ObligationRepository() {
        this(new DriverManagerDataSource("jdbc:h2:mem:" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1"));
    }

    public ObligationRepository(DataSource dataSource) {
        this.jdbc = new JdbcTemplate(dataSource);
        jdbc.execute("""
                CREATE TABLE IF NOT EXISTS obligation (
                    id VARCHAR(20) PRIMARY KEY,
                    sequence_no INT NOT NULL UNIQUE,
                    due_date DATE NOT NULL,
                    effective_due_date DATE NOT NULL,
                    owner_email VARCHAR(255) NOT NULL,
                    compliance_lead_email VARCHAR(255) NOT NULL)
                """);
    }

    public synchronized Obligation add(LocalDate dueDate, LocalDate effectiveDueDate,
                                       String ownerEmail, String complianceLeadEmail) {
        Integer last = jdbc.queryForObject("SELECT MAX(sequence_no) FROM obligation", Integer.class);
        int next = last == null ? FIRST_SEQUENCE : last + 1;
        String id = "obl-" + next;
        jdbc.update("""
                INSERT INTO obligation (id, sequence_no, due_date, effective_due_date, owner_email, compliance_lead_email)
                VALUES (?, ?, ?, ?, ?, ?)
                """, id, next, dueDate, effectiveDueDate, ownerEmail, complianceLeadEmail);
        return new Obligation(id, dueDate, effectiveDueDate, ownerEmail, complianceLeadEmail);
    }

    public Optional<Obligation> findById(String id) {
        return jdbc.query("SELECT * FROM obligation WHERE id = ?", (rs, i) -> toObligation(rs), id)
                .stream().findFirst();
    }

    public List<Obligation> findAll() {
        return jdbc.query("SELECT * FROM obligation ORDER BY sequence_no", (rs, i) -> toObligation(rs));
    }

    private static Obligation toObligation(ResultSet rs) throws SQLException {
        return new Obligation(
                rs.getString("id"),
                rs.getObject("due_date", LocalDate.class),
                rs.getObject("effective_due_date", LocalDate.class),
                rs.getString("owner_email"),
                rs.getString("compliance_lead_email"));
    }
}
