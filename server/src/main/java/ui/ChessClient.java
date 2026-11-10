package ui;

import client.ServerFacade;
import java.util.Scanner;

public class ChessClient {

    private final ServerFacade facade;
    private final Scanner scanner = new Scanner(System.in);
    private boolean running = true;

    public ChessClient(int port) {
        facade = new ServerFacade(port);
    }

    public void run() {
        System.out.println("Welcome to Chess!");
        help();

        while (running) {
            System.out.print("\n>>> ");
            String input = scanner.nextLine().trim().toLowerCase();

            switch (input) {
                case "help" -> help();
                case "quit" -> running = false;
                default -> System.out.println("Unknown command. Type help.");
            }
        }

        System.out.println("Goodbye!");
    }

    private void help() {
        System.out.println("""
                Available commands:
                help     - Show available commands
                quit     - Exit Chess
                """);
    }
}
