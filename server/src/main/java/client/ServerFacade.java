package client;

import com.google.gson.Gson;
import model.AuthData;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import model.GameData;
import java.util.Collection;

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


    public AuthData login(String username, String password) {
        var body = gson.toJson(new LoginRequest(username, password));

        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/session"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        var response = sendRequest(request);

        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), AuthData.class);
        }

        if (response.statusCode() == 401) {
            throw new ServerFacadeException("Invalid username or password");
        }

        if (response.statusCode() == 400) {
            throw new ServerFacadeException("Missing login information");
        }

        throw new ServerFacadeException("Login failed");
    }

    public void logout(String authToken) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/session"))
                .header("authorization", authToken)
                .DELETE()
                .build();

        var response = sendRequest(request);

        if (response.statusCode() == 401) {
            throw new ServerFacadeException("Invalid auth token");
        }

        if (response.statusCode() != 200) {
            throw new ServerFacadeException("Logout failed");
        }
    }

    public int createGame(String gameName, String authToken) {
        var body = gson.toJson(new CreateGameRequest(gameName));

        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/game"))
                .header("Content-Type", "application/json")
                .header("authorization", authToken)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        var response = sendRequest(request);

        if (response.statusCode() == 200) {
            var result = gson.fromJson(
                    response.body(), CreateGameResult.class);
            return result.gameID();
        }

        if (response.statusCode() == 401) {
            throw new ServerFacadeException("Unauthorized");
        }

        if (response.statusCode() == 400) {
            throw new ServerFacadeException("Invalid game name");
        }

        throw new ServerFacadeException("Failed to create game");
    }

    public Collection<GameData> listGames(String authToken) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/game"))
                .header("authorization", authToken)
                .GET()
                .build();

        var response = sendRequest(request);

        if (response.statusCode() == 200) {
            var result = gson.fromJson(
                    response.body(), ListGamesResult.class);
            return result.games();
        }

        if (response.statusCode() == 401) {
            throw new ServerFacadeException("Unauthorized");
        }

        throw new ServerFacadeException("Failed to list games");
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

    public void joinGame(int gameID, String playerColor, String authToken) {
        var body = gson.toJson(new JoinGameRequest(playerColor, gameID));

        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/game"))
                .header("Content-Type", "application/json")
                .header("authorization", authToken)
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .build();

        var response = sendRequest(request);

        if (response.statusCode() == 200) {
            return;
        }

        if (response.statusCode() == 401) {
            throw new ServerFacadeException("Unauthorized");
        }

        if (response.statusCode() == 403) {
            throw new ServerFacadeException("Player color already taken");
        }

        if (response.statusCode() == 400) {
            throw new ServerFacadeException("Invalid game information");
        }

        throw new ServerFacadeException("Failed to join game");
    }

    private record RegisterRequest(
            String username,
            String password,
            String email
    ) {
    }

    private record LoginRequest(
            String username,
            String password
    ) {
    }

    private record CreateGameRequest(String gameName) {
    }

    private record CreateGameResult(int gameID) {
    }

    private record ListGamesResult(Collection<GameData> games) {
    }

    private record JoinGameRequest(String playerColor, int gameID) {
    }
}
