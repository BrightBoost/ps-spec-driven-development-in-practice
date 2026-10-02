package com.example.obligations.config;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * All business dates are evaluated in Europe/Amsterdam (NFR-002, constitution principle I).
 * Setting tracker.fixed-date pins "today", which tests and the demo profile use.
 */
@Configuration
public class ClockConfig {

    public static final ZoneId AMSTERDAM = ZoneId.of("Europe/Amsterdam");

    @Bean
    Clock clock(@Value("${tracker.fixed-date:}") String fixedDate) {
        if (fixedDate == null || fixedDate.isBlank()) {
            return Clock.system(AMSTERDAM);
        }
        LocalDate today = LocalDate.parse(fixedDate);
        return Clock.fixed(today.atTime(LocalTime.NOON).atZone(AMSTERDAM).toInstant(), AMSTERDAM);
    }
}
