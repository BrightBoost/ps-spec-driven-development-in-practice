package com.example.obligations;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.jayway.jsonpath.JsonPath;

/** Small HTTP helper for API tests, so the tests read like the requests a client makes. */
class ApiClient {

    private final HttpClient http = HttpClient.newHttpClient();
    private final String baseUrl;

    ApiClient(int port) {
        this.baseUrl = "http://localhost:" + port;
    }

    HttpResponse<String> get(String path) {
        return send(HttpRequest.newBuilder(URI.create(baseUrl + path)).GET().build());
    }

    HttpResponse<String> post(String path, String json) {
        return send(HttpRequest.newBuilder(URI.create(baseUrl + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build());
    }

    HttpResponse<String> post(String path) {
        return send(HttpRequest.newBuilder(URI.create(baseUrl + path))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build());
    }

    HttpResponse<String> createObligation(String dueDate) {
        return post("/obligations", """
                {"dueDate": "%s", "ownerEmail": "owner@example.com", "complianceLeadEmail": "lead@example.com"}
                """.formatted(dueDate));
    }

    String createAndReturnId(String dueDate) {
        return JsonPath.read(createObligation(dueDate).body(), "$.id");
    }

    private HttpResponse<String> send(HttpRequest request) {
        try {
            return http.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
    }
}
