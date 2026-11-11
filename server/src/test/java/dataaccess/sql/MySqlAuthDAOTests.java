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

    @Test
    void getAuthNotFound() throws DataAccessException {
        assertNull(authDAO.getAuth("invalidToken"));
    }

    @Test
    void deleteAuthSuccess() throws DataAccessException {
        AuthData auth = new AuthData("token123", "ian");

        authDAO.createAuth(auth);
        authDAO.deleteAuth("token123");

        assertNull(authDAO.getAuth("token123"));
    }

    @Test
    void clearAuthSuccess() throws DataAccessException {
        authDAO.createAuth(new AuthData("token1", "ian"));
        authDAO.createAuth(new AuthData("token2", "savanna"));

        authDAO.clear();

        assertNull(authDAO.getAuth("token1"));
        assertNull(authDAO.getAuth("token2"));
    }


}
