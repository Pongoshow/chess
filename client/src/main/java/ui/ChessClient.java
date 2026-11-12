package ui;

import client.ServerFacade;
import client.ServerFacadeException;
import model.GameData;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ChessClient {

    private final ServerFacade facade;
    private final BoardRenderer boardRenderer = new BoardRenderer();
    private final Scanner scanner = new Scanner(System.in);

    private enum State {
        SIGNEDOUT,
        SIGNEDIN,
        QUIT
    }

    private State state = State.SIGNEDOUT;
    private String authToken;
    private List<GameData> listedGames = new ArrayList<>();

    public ChessClient(int port) {
        facade = new ServerFacade(port);
    }

    public void run() {
        System.out.println("Welcome to Chess!");
        help();

        while (state != State.QUIT) {
            System.out.print("\n>>> ");

            if (!scanner.hasNextLine()) {
                break;
            }

            String input = scanner.nextLine().trim().toLowerCase();

            try {
                if (state == State.SIGNEDOUT) {
                    handlePrelogin(input);
                } else {
                    handlePostlogin(input);
                }
            } catch (ServerFacadeException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        System.out.println("Goodbye!");
    }

    // PRELOGIN COMMANDS
    private void handlePrelogin(String input) {
        switch (input) {
            case "help" -> help();
            case "register" -> register();
            case "login" -> login();
            case "quit" -> state = State.QUIT;
            default -> System.out.println("Unknown command. Type help.");
        }
    }

    // POSTLOGIN COMMANDS
    private void handlePostlogin(String input) {
        switch (input) {
            case "help" -> help();
            case "logout" -> logout();
            case "create game" -> createGame();
            case "list games" -> listGames();
            case "play game" -> playGame();
            case "observe game" -> observeGame();
            case "quit" -> state = State.QUIT;
            default -> System.out.println("Unknown command. Type help.");
        }
    }

    // REGISTER
    private void register() {
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine().trim();

        if (username.isBlank() || password.isBlank() || email.isBlank()) {
            System.out.println("Username, password, and email cannot be blank.");
            return;
        }

        var auth = facade.register(username, password, email);

        authToken = auth.authToken();
        state = State.SIGNEDIN;

        System.out.println("Successfully registered! Welcome, " + username);
        help();
    }

    // LOGIN
    private void login() {
        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        var auth = facade.login(username, password);

        authToken = auth.authToken();
        state = State.SIGNEDIN;

        System.out.println("Successfully logged in! Welcome, " + username);
        help();
    }

    // LOGOUT
    private void logout() {
        facade.logout(authToken);

        authToken = null;
        listedGames.clear();
        state = State.SIGNEDOUT;

        System.out.println("Successfully logged out!");
        help();
    }

    // CREATE GAME
    private void createGame() {
        System.out.print("Game name: ");
        String gameName = scanner.nextLine().trim();

        if (gameName.isEmpty()) {
            System.out.println("Game name cannot be empty.");
            return;
        }

        facade.createGame(gameName, authToken);

        System.out.println("Game created successfully!");
    }

    // LIST GAMES
    private void listGames() {
        listedGames = new ArrayList<>(facade.listGames(authToken));

        if (listedGames.isEmpty()) {
            System.out.println("No games available.");
            return;
        }

        System.out.println("Available Games:");

        for (int i = 0; i < listedGames.size(); i++) {
            GameData game = listedGames.get(i);

            String white = game.whiteUsername() == null
                    ? "Open" : game.whiteUsername();

            String black = game.blackUsername() == null
                    ? "Open" : game.blackUsername();

            System.out.printf("%d. %s | White: %s | Black: %s%n",
                    i + 1, game.gameName(), white, black);
        }
    }

    // SELECT GAME
    private GameData selectGame() {
        listGames();

        if (listedGames.isEmpty()) {
            return null;
        }

        System.out.print("Game number: ");
        String input = scanner.nextLine().trim();

        try {
            int number = Integer.parseInt(input);

            if (number < 1 || number > listedGames.size()) {
                System.out.println("Invalid game number.");
                return null;
            }

            return listedGames.get(number - 1);

        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
            return null;
        }
    }

    // PLAY GAME
    private void playGame() {
        GameData game = selectGame();

        if (game == null) {
            return;
        }

        System.out.print("Choose color (WHITE/BLACK): ");
        String color = scanner.nextLine().trim().toUpperCase();

        if (!color.equals("WHITE") && !color.equals("BLACK")) {
            System.out.println("Invalid color. Choose WHITE or BLACK.");
            return;
        }

        facade.joinGame(game.gameID(), color, authToken);

        System.out.println("Successfully joined " +
                game.gameName() + " as " + color);

        showBoard(game, color.equals("BLACK"));
    }

    // OBSERVE GAME
    private void observeGame() {
        GameData game = selectGame();

        if (game == null) {
            return;
        }

        System.out.println("Observing " + game.gameName());

        showBoard(game, false);
    }

    // DISPLAY CHESSBOARD
    private void showBoard(GameData game, boolean blackPerspective) {
        if (game.game() == null || game.game().getBoard() == null) {
            System.out.println("Board data is unavailable.");
            return;
        }

        boardRenderer.draw(game.game().getBoard(), blackPerspective);
    }

    // HELP
    private void help() {
        if (state == State.SIGNEDOUT) {
            System.out.println("""
                    Available commands:
                    help          - Show available commands
                    register      - Create an account
                    login         - Log into an account
                    quit          - Exit Chess
                    """);
        } else {
            System.out.println("""
                    Available commands:
                    help          - Show available commands
                    create game   - Create a chess game
                    list games    - View available games
                    play game     - Join a chess game
                    observe game  - Watch a chess game
                    logout        - Log out of your account
                    quit          - Exit Chess
                    """);
        }
    }
}
