package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;

public class UserService {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthData register(UserData user)
            throws DataAccessException, AlreadyTakenException, BadRequestException {

        if (user == null ||
                user.username() == null || user.username().isBlank() ||
                user.password() == null || user.password().isBlank() ||
                user.email() == null || user.email().isBlank()) {
            throw new BadRequestException("Missing registration information");
        }

        if (userDAO.getUser(user.username()) != null) {
            throw new AlreadyTakenException("Username already taken");
        }

        userDAO.createUser(user);

        AuthData auth = new AuthData(
                UUID.randomUUID().toString(),
                user.username()
        );

        authDAO.createAuth(auth);

        return auth;
    }


    public AuthData login(String username, String password)
            throws DataAccessException, UnauthorizedException, BadRequestException {

        if (username == null || password == null) {
            throw new BadRequestException("Missing login information");
        }

        UserData user = userDAO.getUser(username);

        if (user == null) {
            throw new UnauthorizedException("Invalid username or password");
        }

        // Check the password
        String storedPassword = user.password();
        boolean validPassword;

        if (storedPassword.startsWith("$2a$")) {
            validPassword = BCrypt.checkpw(password, storedPassword);
        } else {
            validPassword = storedPassword.equals(password);
        }

        if (!validPassword) {
            throw new UnauthorizedException("Invalid username or password");
        }

        AuthData auth = new AuthData(
                UUID.randomUUID().toString(),
                username
        );

        authDAO.createAuth(auth);

        return auth;
    }


    public void logout(String authToken)
            throws DataAccessException, UnauthorizedException {

        AuthData auth = authDAO.getAuth(authToken);

        if (auth == null) {
            throw new UnauthorizedException("Invalid auth token");
        }

        authDAO.deleteAuth(authToken);
    }
}