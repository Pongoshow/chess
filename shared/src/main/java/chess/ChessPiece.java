package chess;

import java.util.Collection;
import java.util.ArrayList;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType type;
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

        if (type == PieceType.ROOK){
            //Moving Up
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
                }
                else if (piece.getTeamColor() !=pieceColor) {
                    moves.add(new ChessMove(
                            myPosition,
                            newPosition,
                            null
                    ));
                    break;
                }
                else{
                    break;
                }

                newRow++;
            }
            //Moving Down
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
                }
                else if (piece.getTeamColor() !=pieceColor) {
                    moves.add(new ChessMove(
                            myPosition,
                            newPosition,
                            null
                    ));
                    break;
                }
                else{
                    break;
                }

                newRow--;
            }
            //Moving Right
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
                }
                else if (piece.getTeamColor() !=pieceColor) {
                    moves.add(new ChessMove(
                            myPosition,
                            newPosition,
                            null
                    ));
                    break;
                }
                else{
                    break;
                }

                newCol++;
            }
            //Moving Left
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
                }
                else if (piece.getTeamColor() !=pieceColor) {
                    moves.add(new ChessMove(
                            myPosition,
                            newPosition,
                            null
                    ));
                    break;
                }
                else{
                    break;
                }

                newCol--;
            }
        }

        return moves;
    }
}
