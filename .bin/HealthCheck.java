import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.lang.System;

// This class is used for health checks on the main app.
// This class eliminates the need for any utility (like curl or wget) and relies solely on the Java tooling available in the image.
// It is also portable because it works irrespective of the operating system or underlying base of the image.
//
// https://blog.sixeyed.com/docker-healthchecks-why-not-to-use-curl-or-iwr/
public class HealthCheck {
    public static void main(String[] args) throws InterruptedException, IOException {
        String port = System.getenv("PORT");
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:%s/actuator/health".formatted(port)))
                .header("accept", "application/json")
                .build();
        var response = client.send(request, BodyHandlers.ofString());
        if (response.statusCode() != 200 || !response.body().contains("UP")) {
            throw new RuntimeException("Healthcheck failed");
        }
    }
}
