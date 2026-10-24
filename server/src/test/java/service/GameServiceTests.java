package service;

import dataaccess.*;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {

    @Test
    public void createGameSuccess() throws Exception {
        GameDAO gameDAO = new MemoryGameDAO();
        AuthDAO authDAO = new MemoryAuthDAO();

        authDAO.createAuth(
                new AuthData("token123", "ian")
        );

        GameService service = new GameService(gameDAO, authDAO);

        int gameID = service.createGame(
                "My Game",
                "token123"
        );

        assertTrue(gameID > 0);

        GameData game = gameDAO.getGame(gameID);

        assertNotNull(game);
        assertEquals("My Game", game.gameName());
    }

    @Test
    public void createGameInvalidToken() {
        GameDAO gameDAO = new MemoryGameDAO();
        AuthDAO authDAO = new MemoryAuthDAO();

        GameService service = new GameService(gameDAO, authDAO);

        assertThrows(
                UnauthorizedException.class,
                () -> service.createGame("My Game", "badToken")
        );
    }

    @Test
    public void listGamesSuccess() throws Exception {
        GameDAO gameDAO = new MemoryGameDAO();
        AuthDAO authDAO = new MemoryAuthDAO();

        authDAO.createAuth(
                new AuthData("token123", "ian")
        );

        GameService service = new GameService(gameDAO, authDAO);

        service.createGame("Game One", "token123");
        service.createGame("Game Two", "token123");

        Collection<GameData> games =
                service.listGames("token123");

        assertEquals(2, games.size());
    }

    @Test
    public void joinWhiteSuccess() throws Exception {
        GameDAO gameDAO = new MemoryGameDAO();
        AuthDAO authDAO = new MemoryAuthDAO();

        authDAO.createAuth(
                new AuthData("token123", "ian")
        );

        GameService service = new GameService(gameDAO, authDAO);

        int gameID = service.createGame(
                "My Game",
                "token123"
        );

        service.joinGame(
                gameID,
                "WHITE",
                "token123"
        );

        GameData game = gameDAO.getGame(gameID);

        assertEquals("ian", game.whiteUsername());
    }

    @Test
    public void joinTakenColor() throws Exception {
        GameDAO gameDAO = new MemoryGameDAO();
        AuthDAO authDAO = new MemoryAuthDAO();

        authDAO.createAuth(
                new AuthData("token1", "ian")
        );

        authDAO.createAuth(
                new AuthData("token2", "bob")
        );

        GameService service = new GameService(gameDAO, authDAO);

        int gameID = service.createGame(
                "My Game",
                "token1"
        );

        service.joinGame(
                gameID,
                "WHITE",
                "token1"
        );

        assertThrows(
                AlreadyTakenException.class,
                () -> service.joinGame(
                        gameID,
                        "WHITE",
                        "token2"
                )
        );
    }

    @Test
    public void joinGameDoesNotExist() throws Exception {
        GameDAO gameDAO = new MemoryGameDAO();
        AuthDAO authDAO = new MemoryAuthDAO();

        authDAO.createAuth(
                new AuthData("token123", "ian")
        );

        GameService service = new GameService(gameDAO, authDAO);

        assertThrows(
                BadRequestException.class,
                () -> service.joinGame(
                        999,
                        "WHITE",
                        "token123"
                )
        );
    }

    @Test
    public void createGameBadName() throws Exception {
        GameDAO gameDAO = new MemoryGameDAO();
        AuthDAO authDAO = new MemoryAuthDAO();

        authDAO.createAuth(
                new AuthData("token123", "ian")
        );

        GameService service = new GameService(gameDAO, authDAO);

        assertThrows(
                BadRequestException.class,
                () -> service.createGame("", "token123")
        );
    }

    @Test
    public void joinInvalidColor() throws Exception {
        GameDAO gameDAO = new MemoryGameDAO();
        AuthDAO authDAO = new MemoryAuthDAO();

        authDAO.createAuth(
                new AuthData("token123", "ian")
        );

        GameService service = new GameService(gameDAO, authDAO);

        int gameID = service.createGame(
                "My Game",
                "token123"
        );

        assertThrows(
                BadRequestException.class,
                () -> service.joinGame(
                        gameID,
                        "GREEN",
                        "token123"
                )
        );
    }
}