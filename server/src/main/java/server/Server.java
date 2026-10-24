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
    private final GameService gameService;

    private final ClearService clearService;
    private final UserService userService;

    private final Gson gson = new Gson();

    public Server() {

        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();

        clearService = new ClearService(userDAO, authDAO, gameDAO);
        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(gameDAO, authDAO);

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

        // LOGIN
        javalin.post("/session", ctx -> {
            try {
                LoginRequest request =
                        gson.fromJson(ctx.body(), LoginRequest.class);

                AuthData auth = userService.login(
                        request.username(),
                        request.password()
                );

                LoginResult result = new LoginResult(
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

            } catch (UnauthorizedException e) {
                ctx.status(401);
                ctx.result(
                        "{\"message\":\"Error: unauthorized\"}"
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

        // LOGOUT
        javalin.delete("/session", ctx -> {
            try {
                String authToken = ctx.header("authorization");

                userService.logout(authToken);

                ctx.status(200);
                ctx.result("{}");

            } catch (UnauthorizedException e) {
                ctx.status(401);
                ctx.result(
                        "{\"message\":\"Error: unauthorized\"}"
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

        // LIST GAMES
        javalin.get("/game", ctx -> {
            try {
                String authToken = ctx.header("authorization");

                var games = gameService.listGames(authToken);

                ListGamesResult result = new ListGamesResult(games);

                ctx.status(200);
                ctx.result(gson.toJson(result));

            } catch (UnauthorizedException e) {
                ctx.status(401);
                ctx.result("{\"message\":\"Error: unauthorized\"}");

            } catch (DataAccessException e) {
                ctx.status(500);
                ctx.result(
                        gson.toJson(
                                new ErrorResponse("Error: " + e.getMessage())
                        )
                );
            }
        });

        // CREATE GAME
        javalin.post("/game", ctx -> {
            try {
                String authToken = ctx.header("authorization");

                CreateGameRequest request =
                        gson.fromJson(ctx.body(), CreateGameRequest.class);

                int gameID = gameService.createGame(
                        request.gameName(),
                        authToken
                );

                CreateGameResult result =
                        new CreateGameResult(gameID);

                ctx.status(200);
                ctx.result(gson.toJson(result));

            } catch (BadRequestException e) {
                ctx.status(400);
                ctx.result(
                        "{\"message\":\"Error: bad request\"}"
                );

            } catch (UnauthorizedException e) {
                ctx.status(401);
                ctx.result(
                        "{\"message\":\"Error: unauthorized\"}"
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