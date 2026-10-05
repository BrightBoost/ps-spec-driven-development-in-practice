package com.example.obligations.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.example.obligations.domain.ObligationService;

/**
 * Demo fixture. With the demo profile, "today" is Wednesday 2026-10-07 (application-demo.properties).
 *   obl-100 due Sat 2026-10-10 -> effective Mon 2026-10-12, 3 working days  -> AT_RISK
 *   obl-101 due Tue 2026-10-13 -> 4 working days                             -> ON_TRACK
 *   obl-102 due Fri 2026-10-02 -> in the past                                -> OVERDUE
 *   obl-103 due Wed 2026-10-07 -> due today                                  -> AT_RISK
 */
@Configuration
@Profile("demo")
public class DemoData {

    @Bean
    CommandLineRunner seedObligations(ObligationService service) {
        return args -> {
            service.create("2026-10-10", "sanne@example.com", "compliance.lead@example.com");
            service.create("2026-10-13", "joost@example.com", "compliance.lead@example.com");
            service.create("2026-10-02", "sanne@example.com", "compliance.lead@example.com");
            service.create("2026-10-07", "fatima@example.com", "compliance.lead@example.com");
        };
    }
}
