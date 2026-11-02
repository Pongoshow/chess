
package dataaccess.sql;

import dataaccess.*;
import model.AuthData;

import java.sql.SQLException;

public class MySqlAuthDAO implements AuthDAO {

    @Override
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM auth";

        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(sql)) {

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Failed to clear auth", e);
        }
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        String sql = "INSERT INTO auth (authToken, username) VALUES (?, ?)";

        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(sql)) {

            statement.setString(1, auth.authToken());
            statement.setString(2, auth.username());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Failed to create auth", e);
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        String sql = "SELECT * FROM auth WHERE authToken = ?";

        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(sql)) {

            statement.setString(1, authToken);

            try (var result = statement.executeQuery()) {
                if (result.next()) {
                    return new AuthData(
                            result.getString("authToken"),
                            result.getString("username")
                    );
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException("Failed to get auth", e);
        }

        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        String sql = "DELETE FROM auth WHERE authToken = ?";

        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(sql)) {

            statement.setString(1, authToken);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Failed to delete auth", e);
        }
    }
}
