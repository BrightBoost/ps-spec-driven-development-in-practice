package com.example.obligations;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.http.HttpResponse;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import com.jayway.jsonpath.JsonPath;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "tracker.fixed-date=2026-10-05")
class AcknowledgementTest {

    @Value("${local.server.port}")
    int port;

    ApiClient api;

    @BeforeEach
    void setUp() {
        api = new ApiClient(port);
    }

    @Test
    void ac010_acknowledgesAtRiskObligationWithoutChangingDeadlineState() {
        String id = api.createAndReturnId("2026-10-08");
        String deadlineState = JsonPath.read(api.get("/obligations/" + id).body(), "$.deadlineState");

        HttpResponse<String> response = api.post("/obligations/" + id + "/acknowledgement");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(JsonPath.<String>read(response.body(), "$.escalationState")).isEqualTo("ACKNOWLEDGED");
        assertThat(JsonPath.<String>read(response.body(), "$.deadlineState")).isEqualTo(deadlineState);
        assertThat(response.body()).doesNotContain("escalationRecipients");

        String stored = api.get("/obligations/" + id).body();
        assertThat(JsonPath.<String>read(stored, "$.escalationState")).isEqualTo("ACKNOWLEDGED");
    }

    @Test
    void ac010_acknowledgesOverdueObligationAndItRemainsInDueSoonReport() {
        String id = api.createAndReturnId("2026-10-02");
        String escalated = api.get("/obligations/" + id).body();

        assertThat(JsonPath.<String>read(escalated, "$.escalationState")).isEqualTo("ESCALATED");
        assertThat(JsonPath.<List<String>>read(escalated, "$.escalationRecipients"))
                .containsExactly("owner@example.com", "lead@example.com");

        HttpResponse<String> response = api.post("/obligations/" + id + "/acknowledgement");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(JsonPath.<String>read(response.body(), "$.deadlineState")).isEqualTo("OVERDUE");
        assertThat(JsonPath.<String>read(response.body(), "$.escalationState")).isEqualTo("ACKNOWLEDGED");
        assertThat(response.body()).doesNotContain("escalationRecipients");

        List<String> acknowledgedStates = JsonPath.read(api.get("/obligations/due-soon").body(),
                "$[?(@.id == '%s')].escalationState".formatted(id));
        assertThat(acknowledgedStates).containsExactly("ACKNOWLEDGED");
    }

    @Test
    void ac010_rejectsAcknowledgementWhenObligationIsNotEscalatedWithProblemDetails() {
        String id = api.createAndReturnId("2026-10-09");

        HttpResponse<String> response = api.post("/obligations/" + id + "/acknowledgement");

        assertThat(response.statusCode()).isEqualTo(409);
        assertThat(response.headers().firstValue("Content-Type"))
                .hasValueSatisfying(type -> assertThat(type).startsWith("application/problem+json"));
        assertThat(JsonPath.<Integer>read(response.body(), "$.status")).isEqualTo(409);
    }
}
