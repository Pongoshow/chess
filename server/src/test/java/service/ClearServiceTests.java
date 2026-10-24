package service;

import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import chess.ChessGame;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTests {

    @Test
    public void clearRemovesEverything() throws Exception {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        userDAO.createUser(new UserData("ian", "password", "ian@test.com"));
        authDAO.createAuth(new AuthData("token123", "ian"));

        gameDAO.createGame(new GameData(
                0,
                null,
                null,
                "Test Game",
                new ChessGame()
        ));

        ClearService service = new ClearService(userDAO, authDAO, gameDAO);

        service.clear();

        assertNull(userDAO.getUser("ian"));
        assertNull(authDAO.getAuth("token123"));
        assertTrue(gameDAO.listGames().isEmpty());
    }
}