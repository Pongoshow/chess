package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;

import java.util.Collection;

public class GameService {

    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public Collection<GameData> listGames(String authToken)
            throws DataAccessException, UnauthorizedException {

        verifyAuth(authToken);

        return gameDAO.listGames();
    }

    public int createGame(String gameName, String authToken)
            throws DataAccessException, UnauthorizedException, BadRequestException {

        verifyAuth(authToken);

        if (gameName == null || gameName.isBlank()) {
            throw new BadRequestException("Game name required");
        }

        GameData game = new GameData(
                0,
                null,
                null,
                gameName,
                new ChessGame()
        );

        return gameDAO.createGame(game);
    }

    public void joinGame(int gameID, String playerColor, String authToken)
            throws DataAccessException, UnauthorizedException,
            BadRequestException, AlreadyTakenException {

        AuthData auth = verifyAuth(authToken);

        GameData game = gameDAO.getGame(gameID);

        if (game == null) {
            throw new BadRequestException("Game does not exist");
        }

        if (playerColor == null) {
            throw new BadRequestException("Player color required");
        }

        String whiteUsername = game.whiteUsername();
        String blackUsername = game.blackUsername();

        if (playerColor.equalsIgnoreCase("WHITE")) {
            if (whiteUsername != null) {
                throw new AlreadyTakenException("White already taken");
            }

            whiteUsername = auth.username();

        } else if (playerColor.equalsIgnoreCase("BLACK")) {
            if (blackUsername != null) {
                throw new AlreadyTakenException("Black already taken");
            }

            blackUsername = auth.username();

        } else {
            throw new BadRequestException("Invalid player color");
        }

        GameData updatedGame = new GameData(
                game.gameID(),
                whiteUsername,
                blackUsername,
                game.gameName(),
                game.game()
        );

        gameDAO.updateGame(updatedGame);
    }

    private AuthData verifyAuth(String authToken)
            throws DataAccessException, UnauthorizedException {

        AuthData auth = authDAO.getAuth(authToken);

        if (auth == null) {
            throw new UnauthorizedException("Invalid auth token");
        }

        return auth;
    }
}