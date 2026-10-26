package com.example.obligations.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * In-memory storage for the approved scope. Identifiers are server-assigned (FR-002),
 * so clients can never send a duplicate identifier.
 */
public class ObligationRepository {

    private final Map<String, Obligation> obligations = new ConcurrentHashMap<>();
    private final AtomicInteger sequence = new AtomicInteger(100);

    public Obligation add(LocalDate dueDate, LocalDate effectiveDueDate,
                          String ownerEmail, String complianceLeadEmail) {
        String id = "obl-" + sequence.getAndIncrement();
        Obligation obligation = new Obligation(id, dueDate, effectiveDueDate, ownerEmail, complianceLeadEmail, false);
        obligations.put(id, obligation);
        return obligation;
    }

    public Obligation save(Obligation obligation) {
        obligations.put(obligation.id(), obligation);
        return obligation;
    }

    public Optional<Obligation> findById(String id) {
        return Optional.ofNullable(obligations.get(id));
    }

    public List<Obligation> findAll() {
        return new ArrayList<>(obligations.values());
    }
}
