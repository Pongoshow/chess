
package client;

import com.google.gson.Gson;
import model.AuthData;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServerFacade {

    private final String serverUrl;
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    public ServerFacade(int port) {
        serverUrl = "http://localhost:" + port;
    }

    public AuthData register(String username, String password, String email) {
        var body = gson.toJson(new RegisterRequest(username, password, email));

        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/user"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        var response = sendRequest(request);

        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), AuthData.class);
        }

        if (response.statusCode() == 403) {
            throw new ServerFacadeException("Username already taken");
        }

        if (response.statusCode() == 400) {
            throw new ServerFacadeException("Invalid registration information");
        }

        throw new ServerFacadeException("Registration failed");
    }

    public void clear() {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/db"))
                .DELETE()
                .build();

        var response = sendRequest(request);

        if (response.statusCode() != 200) {
            throw new ServerFacadeException("Failed to clear database");
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (IOException e) {
            throw new ServerFacadeException("Cannot connect to server", e);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ServerFacadeException("Request interrupted", e);
        }
    }

    private record RegisterRequest(
            String username,
            String password,
            String email
    ) {
    }
}
