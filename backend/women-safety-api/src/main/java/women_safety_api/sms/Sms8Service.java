package women_safety_api.sms;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Service;

@Service
public class Sms8Service {

    private static final String SMS8_URL =
            "https://app.sms8.io/api/v1/messages";

    private final HttpClient httpClient =
            HttpClient.newHttpClient();

    public String sendSms(
            String phoneNumber,
            String message) {

        String apiKey =
                System.getenv("SMS8_API_KEY");

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "SMS8_API_KEY environment variable is missing"
            );
        }

        String messagesJson =
                "[{\"number\":\""
                + escapeJson(phoneNumber)
                + "\",\"message\":\""
                + escapeJson(message)
                + "\",\"type\":\"sms\"}]";

        String formData =
                "key=" + encode(apiKey)
                + "&messages=" + encode(messagesJson)
                + "&option=0";

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(SMS8_URL))
                        .header(
                                "Content-Type",
                                "application/x-www-form-urlencoded"
                        )
                        .POST(
                                HttpRequest.BodyPublishers
                                        .ofString(formData)
                        )
                        .build();

        try {

            HttpResponse<String> response =
                    httpClient.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            System.out.println(
                    "SMS8 HTTP status: "
                    + response.statusCode()
            );

            System.out.println(
                    "SMS8 response: "
                    + response.body()
            );

            if (response.statusCode() < 200
                    || response.statusCode() >= 300) {

                throw new RuntimeException(
                        "SMS8 API request failed: "
                        + response.body()
                );
            }

            return response.body();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to send SMS through SMS8",
                    e
            );
        }
    }

    private String encode(String value) {

        return URLEncoder.encode(
                value,
                StandardCharsets.UTF_8
        );
    }

    private String escapeJson(String value) {

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}