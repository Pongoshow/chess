
package dataaccess.sql;

import chess.*;
import dataaccess.DataAccessException;
import model.GameData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MySqlGameDAOTests {

    private final MySqlGameDAO gameDAO = new MySqlGameDAO();

    @BeforeAll
    static void setup() throws DataAccessException {
        MySqlDataAccess.configureDatabase();
    }

    @BeforeEach
    void clear() throws DataAccessException {
        gameDAO.clear();
    }

    private GameData makeGame(String name) {
        ChessGame chessGame = new ChessGame();
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        chessGame.setBoard(board);

        return new GameData(0, null, null, name, chessGame);
    }

    @Test
    void createGameSuccess() throws DataAccessException {
        GameData game = makeGame("Test Game");

        int id = gameDAO.createGame(game);
        GameData saved = gameDAO.getGame(id);

        assertTrue(id > 0);
        assertNotNull(saved);
        assertEquals("Test Game", saved.gameName());
        assertEquals(id, saved.gameID());
    }

    @Test
    void createGameGeneratedIDs() throws DataAccessException {
        int first = gameDAO.createGame(makeGame("Game 1"));
        int second = gameDAO.createGame(makeGame("Game 2"));

        assertNotEquals(first, second);
        assertTrue(second > first);
    }

    @Test
    void getGameNotFound() throws DataAccessException {
        assertNull(gameDAO.getGame(99999));
    }

    @Test
    void listGamesSuccess() throws DataAccessException {
        gameDAO.createGame(makeGame("Game 1"));
        gameDAO.createGame(makeGame("Game 2"));

        var games = gameDAO.listGames();

        assertEquals(2, games.size());
        assertTrue(games.stream().anyMatch(
                game -> game.gameName().equals("Game 1")));
        assertTrue(games.stream().anyMatch(
                game -> game.gameName().equals("Game 2")));
    }

    @Test
    void listGamesEmpty() throws DataAccessException {
        assertTrue(gameDAO.listGames().isEmpty());
    }

    @Test
    void updateGameSuccess() throws DataAccessException {
        int id = gameDAO.createGame(makeGame("Original"));

        GameData updated = new GameData(
                id, "ian", "savanna", "Updated Game",
                makeGame("Updated Game").game()
        );

        gameDAO.updateGame(updated);

        GameData saved = gameDAO.getGame(id);

        assertEquals("ian", saved.whiteUsername());
        assertEquals("savanna", saved.blackUsername());
        assertEquals("Updated Game", saved.gameName());
    }

    @Test
    void updateGameNotFound() {
        GameData game = makeGame("Missing Game");

        assertThrows(DataAccessException.class,
                () -> gameDAO.updateGame(game));
    }

    @Test
    void chessBoardPersists() throws DataAccessException {
        GameData game = makeGame("Chess Board Test");

        int id = gameDAO.createGame(game);
        GameData saved = gameDAO.getGame(id);

        assertNotNull(saved.game());
        assertNotNull(saved.game().getBoard());

        ChessPosition position = new ChessPosition(1, 1);

        ChessPiece piece = saved.game().getBoard().getPiece(position);

        assertNotNull(piece);
        assertEquals(ChessPiece.PieceType.ROOK, piece.getPieceType());
    }

    @Test
    void clearGamesSuccess() throws DataAccessException {
        int id = gameDAO.createGame(makeGame("Test Game"));

        gameDAO.clear();

        assertNull(gameDAO.getGame(id));
        assertTrue(gameDAO.listGames().isEmpty());
    }
}
