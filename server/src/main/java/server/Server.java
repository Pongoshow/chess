package server;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.Javalin;
import io.javalin.http.Context;
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

        javalin = Javalin.create(
                config -> config.staticFiles.add("web")
        );

        javalin.delete("/db", this::clear);
        javalin.post("/user", this::register);
        javalin.post("/session", this::login);
        javalin.delete("/session", this::logout);
        javalin.get("/game", this::listGames);
        javalin.post("/game", this::createGame);
        javalin.put("/game", this::joinGame);
    }

    // CLEAR
    private void clear(Context ctx) {
        try {
            clearService.clear();
            success(ctx);

        } catch (DataAccessException e) {
            serverError(ctx, e);
        }
    }

    // REGISTER
    private void register(Context ctx) {
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

            jsonSuccess(ctx, result);

        } catch (BadRequestException e) {
            badRequest(ctx);

        } catch (AlreadyTakenException e) {
            alreadyTaken(ctx);

        } catch (DataAccessException e) {
            serverError(ctx, e);
        }
    }

    // LOGIN
    private void login(Context ctx) {
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

            jsonSuccess(ctx, result);

        } catch (BadRequestException e) {
            badRequest(ctx);

        } catch (UnauthorizedException e) {
            unauthorized(ctx);

        } catch (DataAccessException e) {
            serverError(ctx, e);
        }
    }

    // LOGOUT
    private void logout(Context ctx) {
        try {
            String authToken = ctx.header("authorization");

            userService.logout(authToken);

            success(ctx);

        } catch (UnauthorizedException e) {
            unauthorized(ctx);

        } catch (DataAccessException e) {
            serverError(ctx, e);
        }
    }

    // LIST GAMES
    private void listGames(Context ctx) {
        try {
            String authToken = ctx.header("authorization");

            var games = gameService.listGames(authToken);

            ListGamesResult result =
                    new ListGamesResult(games);

            jsonSuccess(ctx, result);

        } catch (UnauthorizedException e) {
            unauthorized(ctx);

        } catch (DataAccessException e) {
            serverError(ctx, e);
        }
    }

    // CREATE GAME
    private void createGame(Context ctx) {
        try {
            String authToken = ctx.header("authorization");

            CreateGameRequest request =
                    gson.fromJson(
                            ctx.body(),
                            CreateGameRequest.class
                    );

            int gameID = gameService.createGame(
                    request.gameName(),
                    authToken
            );

            CreateGameResult result =
                    new CreateGameResult(gameID);

            jsonSuccess(ctx, result);

        } catch (BadRequestException e) {
            badRequest(ctx);

        } catch (UnauthorizedException e) {
            unauthorized(ctx);

        } catch (DataAccessException e) {
            serverError(ctx, e);
        }
    }

    // JOIN GAME
    private void joinGame(Context ctx) {
        try {
            String authToken = ctx.header("authorization");

            JoinGameRequest request =
                    gson.fromJson(
                            ctx.body(),
                            JoinGameRequest.class
                    );

            gameService.joinGame(
                    request.gameID(),
                    request.playerColor(),
                    authToken
            );

            success(ctx);

        } catch (BadRequestException e) {
            badRequest(ctx);

        } catch (UnauthorizedException e) {
            unauthorized(ctx);

        } catch (AlreadyTakenException e) {
            alreadyTaken(ctx);

        } catch (DataAccessException e) {
            serverError(ctx, e);
        }
    }

    private void success(Context ctx) {
        ctx.status(200);
        ctx.result("{}");
    }

    private void jsonSuccess(Context ctx, Object result) {
        ctx.status(200);
        ctx.result(gson.toJson(result));
    }

    private void badRequest(Context ctx) {
        ctx.status(400);
        ctx.result(
                "{\"message\":\"Error: bad request\"}"
        );
    }

    private void unauthorized(Context ctx) {
        ctx.status(401);
        ctx.result(
                "{\"message\":\"Error: unauthorized\"}"
        );
    }

    private void alreadyTaken(Context ctx) {
        ctx.status(403);
        ctx.result(
                "{\"message\":\"Error: already taken\"}"
        );
    }

    private void serverError(Context ctx, Exception e) {
        ctx.status(500);
        ctx.result(
                gson.toJson(
                        new ErrorResponse(
                                "Error: " + e.getMessage()
                        )
                )
        );
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