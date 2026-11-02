
package dataaccess.sql;

import dataaccess.DataAccessException;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.*;

public class MySqlUserDAOTests {

    private final MySqlUserDAO userDAO = new MySqlUserDAO();

    @BeforeAll
    static void setup() throws DataAccessException {
        MySqlDataAccess.configureDatabase();
    }

    @BeforeEach
    void clear() throws DataAccessException {
        userDAO.clear();
    }

    @Test
    void createUserSuccess() throws DataAccessException {
        UserData user = new UserData("ian", "password123", "ian@email.com");

        userDAO.createUser(user);

        UserData saved = userDAO.getUser("ian");

        assertNotNull(saved);
        assertEquals("ian", saved.username());
        assertEquals("ian@email.com", saved.email());
    }

    @Test
    void createUserDuplicate() throws DataAccessException {
        UserData user = new UserData("ian", "password123", "ian@email.com");

        userDAO.createUser(user);

        assertThrows(DataAccessException.class,
                () -> userDAO.createUser(user));
    }

    @Test
    void getUserNotFound() throws DataAccessException {
        assertNull(userDAO.getUser("doesNotExist"));
    }

    @Test
    void passwordIsHashed() throws DataAccessException {
        UserData user = new UserData("ian", "password123", "ian@email.com");

        userDAO.createUser(user);

        UserData saved = userDAO.getUser("ian");

        assertNotNull(saved);
        assertNotEquals("password123", saved.password());
        assertTrue(BCrypt.checkpw("password123", saved.password()));
    }

    @Test
    void wrongPasswordFails() throws DataAccessException {
        UserData user = new UserData("ian", "password123", "ian@email.com");

        userDAO.createUser(user);

        UserData saved = userDAO.getUser("ian");

        assertNotNull(saved);
        assertFalse(BCrypt.checkpw("wrongPassword", saved.password()));
    }

    @Test
    void clearUsersSuccess() throws DataAccessException {
        userDAO.createUser(new UserData("ian", "pass1", "ian@email.com"));
        userDAO.createUser(new UserData("savanna", "pass2", "savanna@email.com"));

        userDAO.clear();

        assertNull(userDAO.getUser("ian"));
        assertNull(userDAO.getUser("savanna"));
    }
}
