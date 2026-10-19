package server;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.Javalin;
import model.AuthData;
import model.UserData;
import service.*;

public class Server {

    private final Javalin javalin;

    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    private final ClearService clearService;
    private final UserService userService;

    private final Gson gson = new Gson();

    public Server() {

        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();

        clearService = new ClearService(userDAO, authDAO, gameDAO);
        userService = new UserService(userDAO, authDAO);

        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // CLEAR
        javalin.delete("/db", ctx -> {
            try {
                clearService.clear();

                ctx.status(200);
                ctx.result("{}");

            } catch (DataAccessException e) {
                ctx.status(500);
                ctx.result(
                        "{\"message\":\"Error: " + e.getMessage() + "\"}"
                );
            }
        });

        // REGISTER
        javalin.post("/user", ctx -> {
            try {
                RegisterRequest request =
                        gson.fromJson(ctx.body(), RegisterRequest.class);

                UserData user = new UserData(
                        request.username(),
                        request.password(),
                        request.email()
                );

                AuthData auth = userService.register(user);

                RegisterResult result = new RegisterResult(
                        auth.username(),
                        auth.authToken()
                );

                ctx.status(200);
                ctx.result(gson.toJson(result));

            } catch (BadRequestException e) {
                ctx.status(400);
                ctx.result(
                        "{\"message\":\"Error: bad request\"}"
                );

            } catch (AlreadyTakenException e) {
                ctx.status(403);
                ctx.result(
                        "{\"message\":\"Error: already taken\"}"
                );

            } catch (DataAccessException e) {
                ctx.status(500);
                ctx.result(
                        gson.toJson(
                                new ErrorResponse(
                                        "Error: " + e.getMessage()
                                )
                        )
                );
            }
        });
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private record ErrorResponse(String message) {
    }
}