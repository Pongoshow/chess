package chess;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType type;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece piece = (ChessPiece) o;
        return pieceColor == piece.pieceColor && type == piece.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;

    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        // Rook
        if (type == PieceType.ROOK) {

            // Moving Up
            int newRow = row + 1;

            while (newRow <= 8) {
                ChessPosition newPosition = new ChessPosition(newRow, col);
                ChessPiece piece = board.getPiece(newPosition);

                if (piece == null) {
                    moves.add(new ChessMove(
                            myPosition,
                            newPosition,
                            null
                    ));
                } else if (piece.getTeamColor() != pieceColor) {
                    moves.add(new ChessMove(
                            myPosition,
                            newPosition,
                            null
                    ));
                    break;
                } else {
                    break;
                }

                newRow++;
            }

            // Moving Down
            newRow = row - 1;

            while (newRow >= 1) {
                ChessPosition newPosition = new ChessPosition(newRow, col);
                ChessPiece piece = board.getPiece(newPosition);

                if (piece == null) {
                    moves.add(new ChessMove(
                            myPosition,
                            newPosition,
                            null
                    ));
                } else if (piece.getTeamColor() != pieceColor) {
                    moves.add(new ChessMove(
                            myPosition,
                            newPosition,
                            null
                    ));
                    break;
                } else {
                    break;
                }

                newRow--;
            }

            // Moving Right
            int newCol = col + 1;

            while (newCol <= 8) {
                ChessPosition newPosition = new ChessPosition(row, newCol);
                ChessPiece piece = board.getPiece(newPosition);

                if (piece == null) {
                    moves.add(new ChessMove(
                            myPosition,
                            newPosition,
                            null
                    ));
                } else if (piece.getTeamColor() != pieceColor) {
                    moves.add(new ChessMove(
                            myPosition,
                            newPosition,
                            null
                    ));
                    break;
                } else {
                    break;
                }

                newCol++;
            }

            // Moving Left
            newCol = col - 1;

            while (newCol >= 1) {
                ChessPosition newPosition = new ChessPosition(row, newCol);
                ChessPiece piece = board.getPiece(newPosition);

                if (piece == null) {
                    moves.add(new ChessMove(
                            myPosition,
                            newPosition,
                            null
                    ));
                } else if (piece.getTeamColor() != pieceColor) {
                    moves.add(new ChessMove(
                            myPosition,
                            newPosition,
                            null
                    ));
                    break;
                } else {
                    break;
                }

                newCol--;
            }
        }

        // Bishop
        if (type == PieceType.BISHOP) {

            // Moving UpRight
            int newRow = row + 1;
            int newCol = col + 1;

            while (newRow <= 8 && newCol <= 8) {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessPiece piece = board.getPiece(newPosition);

                if (piece == null) {
                    moves.add(new ChessMove(
                            myPosition,
                            newPosition,
                            null
                    ));
                }
                else if (piece.getTeamColor() != pieceColor) {
                    moves.add(new ChessMove(
                            myPosition,
                            newPosition,
                            null
                    ));
                    break;
                }
                else {
                    break;
                }

                newRow++;
                newCol++;
            }

            // Moving DownRight
            newRow = row - 1;
            newCol = col + 1;

            while (newRow >= 1 && newCol <= 8) {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessPiece piece = board.getPiece(newPosition);

                if (piece == null) {
                    moves.add(new ChessMove(
                            myPosition,
                            newPosition,
                            null
                    ));
                }
                else if (piece.getTeamColor() != pieceColor) {
                    moves.add(new ChessMove(
                            myPosition,
                            newPosition,
                            null
                    ));
                    break;
                }
                else {
                    break;
                }

                newRow--;
                newCol++;
            }

            // Moving UpLeft
            newRow = row + 1;
            newCol = col - 1;

            while (newRow <= 8 && newCol >= 1) {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessPiece piece = board.getPiece(newPosition);

                if (piece == null) {
                    moves.add(new ChessMove(
                            myPosition,
                            newPosition,
                            null
                    ));
                }
                else if (piece.getTeamColor() != pieceColor) {
                    moves.add(new ChessMove(
                            myPosition,
                            newPosition,
                            null
                    ));
                    break;
                }
                else {
                    break;
                }

                newRow++;
                newCol--;
            }

            // Moving DownLeft
            newRow = row - 1;
            newCol = col - 1;

            while (newRow >= 1 && newCol >= 1) {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessPiece piece = board.getPiece(newPosition);

                if (piece == null) {
                    moves.add(new ChessMove(
                            myPosition,
                            newPosition,
                            null
                    ));
                }
                else if (piece.getTeamColor() != pieceColor) {
                    moves.add(new ChessMove(
                            myPosition,
                            newPosition,
                            null
                    ));
                    break;
                }
                else {
                    break;
                }

                newRow--;
                newCol--;
            }
        }

        // Queen
        if (type == PieceType.QUEEN) {

            // Moving Up
            int newRow = row + 1;

            while (newRow <= 8) {
                ChessPosition newPosition = new ChessPosition(newRow, col);
                ChessPiece piece = board.getPiece(newPosition);

                if (piece == null) {
                    moves.add(new ChessMove(
                            myPosition,
                            newPosition,
                            null
                    ));
                } else if (piece.getTeamColor() != pieceColor) {
                    moves.add(new ChessMove(
                            myPosition,
                            newPosition,
                            null
                    ));
                    break;
                } else {
                    break;
                }

                newRow++;
            }

            // Moving Down
            newRow = row - 1;

            while (newRow >= 1) {
                ChessPosition newPosition = new ChessPosition(newRow, col);
                ChessPiece piece = board.getPiece(newPosition);

                if (piece == null) {
                    moves.add(new ChessMove(
                            myPosition,
                            newPosition,
                            null
                    ));
                } else if (piece.getTeamColor() != pieceColor) {
                    moves.add(new ChessMove(
                            myPosition,
                            newPosition,
                            null
                    ));
                    break;
                } else {
                    break;
                }

                newRow--;
            }

            // Moving Right
            int newCol = col + 1;

            while (newCol <= 8) {
                ChessPosition newPosition = new ChessPosition(row, newCol);
                ChessPiece piece = board.getPiece(newPosition);

                if (piece == null) {
                    moves.add(new ChessMove(
                            myPosition,
                            newPosition,
                            null
                    ));
                } else if (piece.getTeamColor() != pieceColor) {
                    moves.add(new ChessMove(
                            myPosition,
                            newPosition,
                            null
                    ));
                    break;
                } else {
                    break;
                }

                newCol++;
            }

            // Moving Left
            newCol = col - 1;

            while (newCol >= 1) {
                ChessPosition newPosition = new ChessPosition(row, newCol);
                ChessPiece piece = board.getPiece(newPosition);

                if (piece == null) {
                    moves.add(new ChessMove(
                            myPosition,
                            newPosition,
                            null
                    ));
                } else if (piece.getTeamColor() != pieceColor) {
                    moves.add(new ChessMove(
                            myPosition,
                            newPosition,
                            null
                    ));
                    break;
                } else {
                    break;
                }

                newCol--;
            }

            // Moving UpRight
            newRow = row + 1;
            newCol = col + 1;

            while (newRow <= 8 && newCol <= 8) {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessPiece piece = board.getPiece(newPosition);

                if (piece == null) {
                    moves.add(new ChessMove(
                            myPosition,
                            newPosition,
                            null
                    ));
                }
                else if (piece.getTeamColor() != pieceColor) {
                    moves.add(new ChessMove(
                            myPosition,
                            newPosition,
                            null
                    ));
                    break;
                }
                else {
                    break;
                }

                newRow++;
                newCol++;
            }

            // Moving DownRight
            newRow = row - 1;
            newCol = col + 1;

            while (newRow >= 1 && newCol <= 8) {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessPiece piece = board.getPiece(newPosition);

                if (piece == null) {
                    moves.add(new ChessMove(
                            myPosition,
                            newPosition,
                            null
                    ));
                }
                else if (piece.getTeamColor() != pieceColor) {
                    moves.add(new ChessMove(
                            myPosition,
                            newPosition,
                            null
                    ));
                    break;
                }
                else {
                    break;
                }

                newRow--;
                newCol++;
            }

            // Moving UpLeft
            newRow = row + 1;
            newCol = col - 1;

            while (newRow <= 8 && newCol >= 1) {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessPiece piece = board.getPiece(newPosition);

                if (piece == null) {
                    moves.add(new ChessMove(
                            myPosition,
                            newPosition,
                            null
                    ));
                }
                else if (piece.getTeamColor() != pieceColor) {
                    moves.add(new ChessMove(
                            myPosition,
                            newPosition,
                            null
                    ));
                    break;
                }
                else {
                    break;
                }

                newRow++;
                newCol--;
            }

            // Moving DownLeft
            newRow = row - 1;
            newCol = col - 1;

            while (newRow >= 1 && newCol >= 1) {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessPiece piece = board.getPiece(newPosition);

                if (piece == null) {
                    moves.add(new ChessMove(
                            myPosition,
                            newPosition,
                            null
                    ));
                }
                else if (piece.getTeamColor() != pieceColor) {
                    moves.add(new ChessMove(
                            myPosition,
                            newPosition,
                            null
                    ));
                    break;
                }
                else {
                    break;
                }

                newRow--;
                newCol--;
            }
        }

        // Pawn
        if (type == PieceType.PAWN) {

            int direction;

            if (pieceColor == ChessGame.TeamColor.WHITE) {
                direction = 1;
            }
            else {
                direction = -1;
            }

            // Moving Forward
            int newRow = row + direction;

            if (newRow >= 1 && newRow <= 8) {
                ChessPosition newPosition = new ChessPosition(newRow, col);
                ChessPiece piece = board.getPiece(newPosition);

                if (piece == null) {

                    // Promotion
                    if ((pieceColor == ChessGame.TeamColor.WHITE && newRow == 8) ||
                            (pieceColor == ChessGame.TeamColor.BLACK && newRow == 1)) {

                        moves.add(new ChessMove(
                                myPosition,
                                newPosition,
                                PieceType.QUEEN
                        ));

                        moves.add(new ChessMove(
                                myPosition,
                                newPosition,
                                PieceType.ROOK
                        ));

                        moves.add(new ChessMove(
                                myPosition,
                                newPosition,
                                PieceType.BISHOP
                        ));

                        moves.add(new ChessMove(
                                myPosition,
                                newPosition,
                                PieceType.KNIGHT
                        ));
                    }
                    else {
                        moves.add(new ChessMove(
                                myPosition,
                                newPosition,
                                null
                        ));
                    }
                }
            }

            // Moving Forward Two Spaces
            if ((pieceColor == ChessGame.TeamColor.WHITE && row == 2) ||
                    (pieceColor == ChessGame.TeamColor.BLACK && row == 7)) {

                int firstRow = row + direction;
                int secondRow = row + (direction * 2);

                ChessPosition firstPosition = new ChessPosition(firstRow, col);
                ChessPosition secondPosition = new ChessPosition(secondRow, col);

                ChessPiece firstPiece = board.getPiece(firstPosition);
                ChessPiece secondPiece = board.getPiece(secondPosition);

                if (firstPiece == null && secondPiece == null) {
                    moves.add(new ChessMove(
                            myPosition,
                            secondPosition,
                            null
                    ));
                }
            }

            // Attacking Left
            int newCol = col - 1;

            if (newCol >= 1 && newRow >= 1 && newRow <= 8) {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessPiece piece = board.getPiece(newPosition);

                if (piece != null && piece.getTeamColor() != pieceColor) {

                    // Promotion
                    if ((pieceColor == ChessGame.TeamColor.WHITE && newRow == 8) ||
                            (pieceColor == ChessGame.TeamColor.BLACK && newRow == 1)) {

                        moves.add(new ChessMove(
                                myPosition,
                                newPosition,
                                PieceType.QUEEN
                        ));

                        moves.add(new ChessMove(
                                myPosition,
                                newPosition,
                                PieceType.ROOK
                        ));

                        moves.add(new ChessMove(
                                myPosition,
                                newPosition,
                                PieceType.BISHOP
                        ));

                        moves.add(new ChessMove(
                                myPosition,
                                newPosition,
                                PieceType.KNIGHT
                        ));
                    }
                    else {
                        moves.add(new ChessMove(
                                myPosition,
                                newPosition,
                                null
                        ));
                    }
                }
            }

            // Attacking Right
            newCol = col + 1;

            if (newCol <= 8 && newRow >= 1 && newRow <= 8) {
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                ChessPiece piece = board.getPiece(newPosition);

                if (piece != null && piece.getTeamColor() != pieceColor) {

                    // Promotion
                    if ((pieceColor == ChessGame.TeamColor.WHITE && newRow == 8) ||
                            (pieceColor == ChessGame.TeamColor.BLACK && newRow == 1)) {

                        moves.add(new ChessMove(
                                myPosition,
                                newPosition,
                                PieceType.QUEEN
                        ));

                        moves.add(new ChessMove(
                                myPosition,
                                newPosition,
                                PieceType.ROOK
                        ));

                        moves.add(new ChessMove(
                                myPosition,
                                newPosition,
                                PieceType.BISHOP
                        ));

                        moves.add(new ChessMove(
                                myPosition,
                                newPosition,
                                PieceType.KNIGHT
                        ));
                    }
                    else {
                        moves.add(new ChessMove(
                                myPosition,
                                newPosition,
                                null
                        ));
                    }
                }
            }
        }

        // King
        if (type == PieceType.KING) {

            for (int rowChange = -1; rowChange <= 1; rowChange++) {
                for (int colChange = -1; colChange <= 1; colChange++) {

                    if (rowChange == 0 && colChange == 0) {
                        continue;
                    }

                    int newRow = row + rowChange;
                    int newCol = col + colChange;

                    if (newRow >= 1 && newRow <= 8 &&
                            newCol >= 1 && newCol <= 8) {

                        ChessPosition newPosition = new ChessPosition(newRow, newCol);
                        ChessPiece piece = board.getPiece(newPosition);

                        if (piece == null) {
                            moves.add(new ChessMove(
                                    myPosition,
                                    newPosition,
                                    null
                            ));
                        }
                        else if (piece.getTeamColor() != pieceColor) {
                            moves.add(new ChessMove(
                                    myPosition,
                                    newPosition,
                                    null
                            ));
                        }
                    }
                }
            }
        }

        // Knight
        if (type == PieceType.KNIGHT) {

            int[][] changes = {
                    {2, 1},
                    {2, -1},
                    {-2, 1},
                    {-2, -1},
                    {1, 2},
                    {1, -2},
                    {-1, 2},
                    {-1, -2}
            };

            for (int[] change : changes) {
                int newRow = row + change[0];
                int newCol = col + change[1];

                if (newRow >= 1 && newRow <= 8 &&
                        newCol >= 1 && newCol <= 8) {

                    ChessPosition newPosition = new ChessPosition(newRow, newCol);
                    ChessPiece piece = board.getPiece(newPosition);

                    if (piece == null) {
                        moves.add(new ChessMove(
                                myPosition,
                                newPosition,
                                null
                        ));
                    }
                    else if (piece.getTeamColor() != pieceColor) {
                        moves.add(new ChessMove(
                                myPosition,
                                newPosition,
                                null
                        ));
                    }
                }
            }
        }

        return moves;
    }
}
