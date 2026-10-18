package com.example.obligations;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import com.jayway.jsonpath.JsonPath;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "tracker.fixed-date=2026-10-05")
class DueSoonReportTest {

    @Value("${local.server.port}")
    int port;

    ApiClient api;

    @BeforeEach
    void setUp() {
        api = new ApiClient(port);
    }

    @Test
    void ac008_reportContainsAtRiskObligations() {
        String atRisk = api.createAndReturnId("2026-10-07");

        List<String> ids = JsonPath.read(api.get("/obligations/due-soon").body(), "$[*].id");

        assertThat(ids).contains(atRisk);
    }

    @Test
    void ac008_reportIncludesOverdueAndOrdersEarliestEffectiveDueDateFirst() {
        // Created in reverse order on purpose
        String laterAtRisk = api.createAndReturnId("2026-10-08");
        String soonerAtRisk = api.createAndReturnId("2026-10-06");
        String overdue = api.createAndReturnId("2026-09-30");
        String onTrack = api.createAndReturnId("2026-10-20");

        List<String> ids = JsonPath.read(api.get("/obligations/due-soon").body(), "$[*].id");

        assertThat(ids).contains(overdue, soonerAtRisk, laterAtRisk).doesNotContain(onTrack);
        assertThat(ids.indexOf(overdue)).isLessThan(ids.indexOf(soonerAtRisk));
        assertThat(ids.indexOf(soonerAtRisk)).isLessThan(ids.indexOf(laterAtRisk));
    }
}
