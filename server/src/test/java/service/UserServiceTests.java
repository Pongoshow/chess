package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {

    @Test
    public void registerSuccess() throws Exception {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();

        UserService service = new UserService(userDAO, authDAO);

        UserData user = new UserData(
                "ian",
                "password",
                "ian@test.com"
        );

        AuthData result = service.register(user);

        assertEquals("ian", result.username());
        assertNotNull(result.authToken());
        assertNotNull(userDAO.getUser("ian"));
    }

    @Test
    public void registerUsernameAlreadyTaken() throws Exception {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();

        UserService service = new UserService(userDAO, authDAO);

        UserData user = new UserData(
                "ian",
                "password",
                "ian@test.com"
        );

        service.register(user);

        assertThrows(
                AlreadyTakenException.class,
                () -> service.register(user)
        );
    }

    @Test
    public void loginSuccess() throws Exception {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();

        UserService service = new UserService(userDAO, authDAO);

        userDAO.createUser(
                new UserData(
                        "ian",
                        "password",
                        "ian@test.com"
                )
        );

        AuthData result = service.login("ian", "password");

        assertEquals("ian", result.username());
        assertNotNull(result.authToken());
    }

    @Test
    public void loginWrongPassword() throws Exception {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();

        UserService service = new UserService(userDAO, authDAO);

        userDAO.createUser(
                new UserData(
                        "ian",
                        "password",
                        "ian@test.com"
                )
        );

        assertThrows(
                UnauthorizedException.class,
                () -> service.login("ian", "wrongPassword")
        );
    }

    @Test
    public void logoutSuccess() throws Exception {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();

        UserService service = new UserService(userDAO, authDAO);

        authDAO.createAuth(
                new AuthData("token123", "ian")
        );

        service.logout("token123");

        assertNull(authDAO.getAuth("token123"));
    }

    @Test
    public void logoutInvalidToken() {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();

        UserService service = new UserService(userDAO, authDAO);

        assertThrows(
                UnauthorizedException.class,
                () -> service.logout("badToken")
        );
    }
}