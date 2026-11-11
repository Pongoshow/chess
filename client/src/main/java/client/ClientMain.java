package client;

import ui.ChessClient;

public class ClientMain {

    public static void main(String[] args) {
        ChessClient chess = new ChessClient(8080);
        chess.run();
    }
}
