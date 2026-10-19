package server;

import dataaccess.*;
import io.javalin.Javalin;
import service.ClearService;

public class Server {

    private final Javalin javalin;

    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    private final ClearService clearService;

    public Server() {

        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();

        clearService = new ClearService(userDAO, authDAO, gameDAO);

        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        javalin.delete("/db", ctx -> {
            try {
                clearService.clear();
                ctx.status(200).json(new Object());
            } catch (DataAccessException e) {
                ctx.status(500).json(
                        new ErrorResponse("Error: " + e.getMessage())
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