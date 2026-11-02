
package dataaccess.sql;

import dataaccess.DataAccessException;
import model.AuthData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MySqlAuthDAOTests {

    private final MySqlAuthDAO authDAO = new MySqlAuthDAO();

    @BeforeAll
    static void setup() throws DataAccessException {
        MySqlDataAccess.configureDatabase();
    }

    @BeforeEach
    void clear() throws DataAccessException {
        authDAO.clear();
    }

    @Test
    void createAuthSuccess() throws DataAccessException {
        AuthData auth = new AuthData("token123", "ian");

        authDAO.createAuth(auth);

        assertEquals(auth, authDAO.getAuth("token123"));
    }

    @Test
    void createAuthDuplicate() throws DataAccessException {
        AuthData auth = new AuthData("token123", "ian");

        authDAO.createAuth(auth);

        assertThrows(DataAccessException.class,
                () -> authDAO.createAuth(auth));
    }


}
