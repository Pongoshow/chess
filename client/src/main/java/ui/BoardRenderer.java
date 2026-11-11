package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

public class BoardRenderer {

    // Board colors
    private static final String LIGHT_SQUARE = "\u001B[48;5;223m";
    private static final String DARK_SQUARE = "\u001B[48;5;180m";

    // Piece colors
    private static final String WHITE_PIECES = "\u001B[38;5;88m";
    private static final String BLACK_PIECES = "\u001B[38;5;18m";

    public void draw(ChessBoard board, boolean blackPerspective) {
        if (board == null) {
            System.out.println("Board is unavailable.");
            return;
        }

        System.out.println();
        printLetters(blackPerspective);

        for (int i = 0; i < 8; i++) {
            int row = blackPerspective ? i + 1 : 8 - i;

            System.out.print(" " + row + " ");

            for (int j = 0; j < 8; j++) {
                int col = blackPerspective ? 8 - j : j + 1;

                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);

                boolean lightSquare = (row + col) % 2 != 0;

                // Set square background
                System.out.print(lightSquare ? LIGHT_SQUARE : DARK_SQUARE);

                // Set piece color based on team
                if (piece != null) {
                    if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                        System.out.print(WHITE_PIECES);
                    } else {
                        System.out.print(BLACK_PIECES);
                    }
                }

                System.out.print(getPieceSymbol(piece));

                // Reset colors
                System.out.print(EscapeSequences.RESET_TEXT_COLOR);
                System.out.print(EscapeSequences.RESET_BG_COLOR);
            }

            System.out.println(" " + row);
        }

        printLetters(blackPerspective);
        System.out.println();
    }

    private void printLetters(boolean blackPerspective) {
        System.out.print("   ");

        for (int i = 0; i < 8; i++) {
            char letter = (char) ('a' + (blackPerspective ? 7 - i : i));
            System.out.print("  " + letter + " ");
        }

        System.out.println();
    }

    private String getPieceSymbol(ChessPiece piece) {
        if (piece == null) {
            return EscapeSequences.EMPTY;
        }

        boolean white = piece.getTeamColor() == ChessGame.TeamColor.WHITE;

        return switch (piece.getPieceType()) {
            case KING -> white
                    ? EscapeSequences.WHITE_KING
                    : EscapeSequences.BLACK_KING;

            case QUEEN -> white
                    ? EscapeSequences.WHITE_QUEEN
                    : EscapeSequences.BLACK_QUEEN;

            case ROOK -> white
                    ? EscapeSequences.WHITE_ROOK
                    : EscapeSequences.BLACK_ROOK;

            case BISHOP -> white
                    ? EscapeSequences.WHITE_BISHOP
                    : EscapeSequences.BLACK_BISHOP;

            case KNIGHT -> white
                    ? EscapeSequences.WHITE_KNIGHT
                    : EscapeSequences.BLACK_KNIGHT;

            case PAWN -> white
                    ? EscapeSequences.WHITE_PAWN
                    : EscapeSequences.BLACK_PAWN;
        };
    }
}
