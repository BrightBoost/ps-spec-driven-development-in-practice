package com.example.obligations;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.http.HttpResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import com.jayway.jsonpath.JsonPath;

/** Current date for every test: Monday 2026-10-05 in Europe/Amsterdam. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "tracker.fixed-date=2026-10-05")
class ObligationDeadlineControllerTest {

    @Value("${local.server.port}")
    int port;

    ApiClient api;

    @BeforeEach
    void setUp() {
        api = new ApiClient(port);
    }

    @Test
    void ac001_createsObligationWithServerAssignedIdAndEffectiveDueDate() {
        HttpResponse<String> response = api.createObligation("2026-10-08");

        assertThat(response.statusCode()).isEqualTo(201);
        assertThat(JsonPath.<String>read(response.body(), "$.id")).startsWith("obl-");
        assertThat(JsonPath.<String>read(response.body(), "$.effectiveDueDate")).isEqualTo("2026-10-08");
    }

    @Test
    void ac002_rejectsInvalidDueDateWithProblemDetails() {
        HttpResponse<String> response = api.createObligation("2026-13-45");

        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(response.headers().firstValue("Content-Type")).hasValueSatisfying(
                type -> assertThat(type).startsWith("application/problem+json"));
    }

    @Test
    void ac003_isAtRiskAtExactlyThreeWorkingDays() {
        String id = api.createAndReturnId("2026-10-08");

        HttpResponse<String> response = api.get("/obligations/" + id);

        assertThat(response).isNotNull();
    }

    @Test
    void ac004_rollsWeekendDueDateForwardToNextWorkingDay() {
        HttpResponse<String> response = api.createObligation("2026-10-10");

        assertThat(JsonPath.<String>read(response.body(), "$.effectiveDueDate")).isEqualTo("2026-10-12");
    }

    @Test
    void ac006_isOverdueWhenEffectiveDueDateIsInThePast() {
        String id = api.createAndReturnId("2026-10-02");

        HttpResponse<String> response = api.get("/obligations/" + id);

        assertThat(JsonPath.<String>read(response.body(), "$.deadlineState")).isEqualTo("OVERDUE");
    }

    @Test
    void ac007_escalationRecipientsAreReturnedWhenAtRisk() {
        String id = api.createAndReturnId("2026-10-06");

        HttpResponse<String> response = api.get("/obligations/" + id);

        assertThat(JsonPath.<String>read(response.body(), "$.escalationState")).isEqualTo("ESCALATED");
    }

    @Test
    void ac009_returnsNotFoundForUnknownIdentifier() {
        HttpResponse<String> response = api.get("/obligations/obl-999999");

        assertThat(response.statusCode()).isEqualTo(404);
    }
}
