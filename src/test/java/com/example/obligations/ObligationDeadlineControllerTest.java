package com.example.obligations;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.http.HttpResponse;
import java.util.List;

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

        assertProblem(response, 400);
        assertThat(JsonPath.<String>read(response.body(), "$.field")).isEqualTo("dueDate");
    }

    @Test
    void ac002_rejectsBlankOwnerEmailWithProblemDetails() {
        HttpResponse<String> response = api.post("/obligations", """
                {"dueDate": "2026-10-08", "ownerEmail": " ", "complianceLeadEmail": "lead@example.com"}
                """);

        assertProblem(response, 400);
        assertThat(JsonPath.<String>read(response.body(), "$.field")).isEqualTo("ownerEmail");
    }

    @Test
    void ac002_rejectsBlankComplianceLeadEmailWithProblemDetails() {
        HttpResponse<String> response = api.post("/obligations", """
                {"dueDate": "2026-10-08", "ownerEmail": "owner@example.com", "complianceLeadEmail": ""}
                """);

        assertProblem(response, 400);
        assertThat(JsonPath.<String>read(response.body(), "$.field")).isEqualTo("complianceLeadEmail");
    }

    @Test
    void ac003_isAtRiskAtExactlyThreeWorkingDays() {
        // Monday 2026-10-05 -> Thursday 2026-10-08 is three working days
        String id = api.createAndReturnId("2026-10-08");

        String body = api.get("/obligations/" + id).body();

        assertThat(JsonPath.<String>read(body, "$.effectiveDueDate")).isEqualTo("2026-10-08");
        assertThat(JsonPath.<String>read(body, "$.deadlineState")).isEqualTo("AT_RISK");
        assertThat(JsonPath.<String>read(body, "$.escalationState")).isEqualTo("ESCALATED");
        assertThat(JsonPath.<List<String>>read(body, "$.escalationRecipients"))
                .containsExactly("owner@example.com", "lead@example.com");
    }

    @Test
    void ac005_isOnTrackAtFourWorkingDays() {
        // Monday 2026-10-05 -> Friday 2026-10-09 is four working days
        String id = api.createAndReturnId("2026-10-09");

        String body = api.get("/obligations/" + id).body();

        assertThat(JsonPath.<String>read(body, "$.deadlineState")).isEqualTo("ON_TRACK");
        assertThat(JsonPath.<String>read(body, "$.escalationState")).isEqualTo("NONE");
        assertThat(JsonPath.<List<String>>read(body, "$.escalationRecipients")).isEmpty();
    }

    @Test
    void ac004_rollsWeekendDueDateForwardToNextWorkingDay() {
        HttpResponse<String> response = api.createObligation("2026-10-10");

        assertThat(JsonPath.<String>read(response.body(), "$.effectiveDueDate")).isEqualTo("2026-10-12");
    }

    @Test
    void ac004_skipsAHolidayMondayAfterTheWeekend() {
        HttpResponse<String> response = api.createObligation("2026-04-26");

        assertThat(JsonPath.<String>read(response.body(), "$.effectiveDueDate")).isEqualTo("2026-04-28");
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
        assertThat(JsonPath.<List<String>>read(response.body(), "$.escalationRecipients"))
                .containsExactly("owner@example.com", "lead@example.com");
    }

    @Test
    void ac009_returnsNotFoundForUnknownIdentifier() {
        HttpResponse<String> response = api.get("/obligations/obl-999999");

        assertProblem(response, 404);
    }

    private static void assertProblem(HttpResponse<String> response, int status) {
        assertThat(response.statusCode()).isEqualTo(status);
        assertThat(response.headers().firstValue("Content-Type")).hasValueSatisfying(
                type -> assertThat(type).startsWith("application/problem+json"));
        assertThat(JsonPath.<Integer>read(response.body(), "$.status")).isEqualTo(status);
    }
}
