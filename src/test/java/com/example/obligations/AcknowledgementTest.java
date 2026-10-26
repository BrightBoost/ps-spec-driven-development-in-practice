package com.example.obligations;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.http.HttpResponse;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import com.jayway.jsonpath.JsonPath;

/** AC-010 (DEC-003). Current date: Monday 2026-10-05. */
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
    void ac010_acknowledgingAnEscalatedObligationKeepsItsDeadlineState() {
        String id = api.createAndReturnId("2026-10-08");

        HttpResponse<String> response = api.post("/obligations/" + id + "/acknowledgement", "");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(JsonPath.<String>read(response.body(), "$.escalationState")).isEqualTo("ACKNOWLEDGED");
        assertThat(JsonPath.<String>read(response.body(), "$.deadlineState")).isEqualTo("AT_RISK");
    }

    @Test
    void ac010_acknowledgingAnObligationThatIsNotEscalatedIsAConflict() {
        String id = api.createAndReturnId("2026-10-20");

        HttpResponse<String> response = api.post("/obligations/" + id + "/acknowledgement", "");

        assertThat(response.statusCode()).isEqualTo(409);
        assertThat(response.headers().firstValue("Content-Type")).hasValueSatisfying(
                type -> assertThat(type).startsWith("application/problem+json"));
    }

    @Test
    void ac008_acknowledgedObligationsStayInTheDueSoonReport() {
        String id = api.createAndReturnId("2026-10-07");
        api.post("/obligations/" + id + "/acknowledgement", "");

        String report = api.get("/obligations/due-soon").body();

        List<String> states = JsonPath.read(report, "$[?(@.id == '" + id + "')].escalationState");
        assertThat(states).containsExactly("ACKNOWLEDGED");
    }
}
