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



}
