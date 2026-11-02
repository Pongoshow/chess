
package dataaccess.sql;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.*;
import model.GameData;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;

public class MySqlGameDAO implements GameDAO {

    private final Gson gson = new Gson();

    @Override
    public void clear() throws DataAccessException {
        String sql = "TRUNCATE TABLE games";

        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(sql)) {

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Failed to clear games", e);
        }
    }

    @Override
    public int createGame(GameData game) throws DataAccessException {
        String sql = """
                INSERT INTO games
                (whiteUsername, blackUsername, gameName, game)
                VALUES (?, ?, ?, ?)
                """;

        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, game.whiteUsername());
            statement.setString(2, game.blackUsername());
            statement.setString(3, game.gameName());
            statement.setString(4, gson.toJson(game.game()));

            statement.executeUpdate();

            try (var keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }

            throw new DataAccessException("Failed to generate game ID");

        } catch (SQLException e) {
            throw new DataAccessException("Failed to create game", e);
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        String sql = "SELECT * FROM games WHERE gameID = ?";

        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(sql)) {

            statement.setInt(1, gameID);

            try (var result = statement.executeQuery()) {
                if (result.next()) {
                    return readGame(result);
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException("Failed to get game", e);
        }

        return null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        Collection<GameData> games = new ArrayList<>();
        String sql = "SELECT * FROM games ORDER BY gameID";

        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(sql);
             var result = statement.executeQuery()) {

            while (result.next()) {
                games.add(readGame(result));
            }

        } catch (SQLException e) {
            throw new DataAccessException("Failed to list games", e);
        }

        return games;
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        String sql = """
                UPDATE games
                SET whiteUsername = ?, blackUsername = ?,
                    gameName = ?, game = ?
                WHERE gameID = ?
                """;

        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(sql)) {

            statement.setString(1, game.whiteUsername());
            statement.setString(2, game.blackUsername());
            statement.setString(3, game.gameName());
            statement.setString(4, gson.toJson(game.game()));
            statement.setInt(5, game.gameID());

            if (statement.executeUpdate() == 0) {
                throw new DataAccessException("Game does not exist");
            }

        } catch (SQLException e) {
            throw new DataAccessException("Failed to update game", e);
        }
    }

    private GameData readGame(ResultSet result) throws SQLException {
        ChessGame chessGame = gson.fromJson(
                result.getString("game"),
                ChessGame.class
        );

        return new GameData(
                result.getInt("gameID"),
                result.getString("whiteUsername"),
                result.getString("blackUsername"),
                result.getString("gameName"),
                chessGame
        );
    }
}
