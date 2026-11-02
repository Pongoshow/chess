
package dataaccess.sql;

import dataaccess.*;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

public class MySqlUserDAO implements UserDAO {

    @Override
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM users";

        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(sql)) {

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Failed to clear users", e);
        }
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";

        String hashedPassword = BCrypt.hashpw(
                user.password(), BCrypt.gensalt()
        );

        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(sql)) {

            statement.setString(1, user.username());
            statement.setString(2, hashedPassword);
            statement.setString(3, user.email());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Failed to create user", e);
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(sql)) {

            statement.setString(1, username);

            try (var result = statement.executeQuery()) {
                if (result.next()) {
                    return new UserData(
                            result.getString("username"),
                            result.getString("password"),
                            result.getString("email")
                    );
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException("Failed to get user", e);
        }

        return null;
    }
}
