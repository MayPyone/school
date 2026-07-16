package com.school.school;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserSignupApiTests {

    @LocalServerPort
    private int port;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Test
    void signupWithValidPayloadReturnsCreated() throws Exception {
        String email = "signup-test-" + System.nanoTime() + "@example.com";

        HttpResponse<String> response = postSignup("""
                {
                  "firstName": "Test",
                  "lastName": "User",
                  "email": "%s",
                  "role": "END_USER",
                  "password": "secret123"
                }
                """.formatted(email));

        assertEquals(201, response.statusCode(), response.body());
    }

    @Test
    void signupWithMissingFieldsReturnsBadRequest() throws Exception {
        HttpResponse<String> response = postSignup("""
                {
                  "email": "missing-fields@example.com",
                  "password": "secret123"
                }
                """);

        assertEquals(400, response.statusCode(), response.body());
    }

    private HttpResponse<String> postSignup(String body) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/v1/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
