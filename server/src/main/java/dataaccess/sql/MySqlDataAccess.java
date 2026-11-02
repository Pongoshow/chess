
package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import java.sql.SQLException;

public class MySqlDataAccess {

    public static void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();

        try (var conn = DatabaseManager.getConnection();
             var statement = conn.createStatement()) {

            // Users table
            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS users (
                    username VARCHAR(255) PRIMARY KEY,
                    password VARCHAR(255) NOT NULL,
                    email VARCHAR(255) NOT NULL
                )
                """);

            // Authentication table
            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS auth (
                    authToken VARCHAR(255) PRIMARY KEY,
                    username VARCHAR(255) NOT NULL
                )
                """);

            // Games table
            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS games (
                    gameID INT AUTO_INCREMENT PRIMARY KEY,
                    whiteUsername VARCHAR(255),
                    blackUsername VARCHAR(255),
                    gameName VARCHAR(255) NOT NULL,
                    game TEXT NOT NULL
                )
                """);

        } catch (SQLException e) {
            throw new DataAccessException("Unable to configure database", e);
        }
    }
}
