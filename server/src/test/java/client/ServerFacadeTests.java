package client;

import org.junit.jupiter.api.*;
import server.Server;
import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @BeforeEach
    void clearDatabase() {
        facade.clear();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }


    @Test
    void registerSuccess() {
        var result = facade.register("ian", "password123", "ian@email.com");

        assertNotNull(result);
        assertEquals("ian", result.username());
        assertNotNull(result.authToken());
        assertFalse(result.authToken().isEmpty());
    }

    @Test
    void registerDuplicate() {
        facade.register("ian", "password123", "ian@email.com");

        assertThrows(ServerFacadeException.class, () ->
                facade.register("ian", "password123", "ian@email.com"));
    }

    @Test
    void loginSuccess() {
        facade.register("ian", "password123", "ian@email.com");

        var result = facade.login("ian", "password123");

        assertNotNull(result);
        assertEquals("ian", result.username());
        assertNotNull(result.authToken());
    }

    @Test
    void loginWrongPassword() {
        facade.register("ian", "password123", "ian@email.com");

        assertThrows(ServerFacadeException.class, () ->
                facade.login("ian", "wrongPassword"));
    }

    @Test
    void loginUserNotFound() {
        assertThrows(ServerFacadeException.class, () ->
                facade.login("unknown", "password123"));
    }

    @Test
    void logoutSuccess() {
        var auth = facade.register("ian", "password123", "ian@email.com");

        assertDoesNotThrow(() -> facade.logout(auth.authToken()));

        // Logging out again should fail
        assertThrows(ServerFacadeException.class, () ->
                facade.logout(auth.authToken()));
    }

    @Test
    void logoutInvalidToken() {
        assertThrows(ServerFacadeException.class, () ->
                facade.logout("invalidToken"));
    }

    @Test
    void createGameSuccess() {
        var auth = facade.register(
                "ian", "password123", "ian@email.com");

        int gameID = facade.createGame(
                "My Chess Game", auth.authToken());

        assertTrue(gameID > 0);
    }

    @Test
    void createGameUnauthorized() {
        assertThrows(ServerFacadeException.class, () ->
                facade.createGame("My Chess Game", "invalidToken"));
    }

    @Test
    void createGameInvalidName() {
        var auth = facade.register(
                "ian", "password123", "ian@email.com");

        assertThrows(ServerFacadeException.class, () ->
                facade.createGame(null, auth.authToken()));
    }

    @Test
    void listGamesSuccess() {
        var auth = facade.register("ian", "password123", "ian@email.com");

        facade.createGame("Game 1", auth.authToken());
        facade.createGame("Game 2", auth.authToken());

        var games = facade.listGames(auth.authToken());

        assertEquals(2, games.size());
        assertTrue(games.stream().anyMatch(
                game -> game.gameName().equals("Game 1")));
        assertTrue(games.stream().anyMatch(
                game -> game.gameName().equals("Game 2")));
    }

    @Test
    void listGamesEmpty() {
        var auth = facade.register("ian", "password123", "ian@email.com");

        var games = facade.listGames(auth.authToken());

        assertTrue(games.isEmpty());
    }

    @Test
    void listGamesUnauthorized() {
        assertThrows(ServerFacadeException.class, () ->
                facade.listGames("invalidToken"));
    }


}
