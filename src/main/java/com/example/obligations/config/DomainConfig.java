package com.example.obligations.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.obligations.calendar.WorkingDayCalendar;
import com.example.obligations.domain.DeadlineStatePolicy;
import com.example.obligations.domain.ObligationRepository;

/** Domain classes stay free of framework annotations; they are wired here. */
@Configuration
public class DomainConfig {

    @Bean
    WorkingDayCalendar workingDayCalendar() {
        return new WorkingDayCalendar();
    }

    @Bean
    DeadlineStatePolicy deadlineStatePolicy(WorkingDayCalendar calendar) {
        return new DeadlineStatePolicy(calendar);
    }

    @Bean
    ObligationRepository obligationRepository() {
        return new ObligationRepository();
    }
}
