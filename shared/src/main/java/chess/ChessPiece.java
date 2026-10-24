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

        if (type == PieceType.ROOK) {
            return rookMoves(board, myPosition);
        }

        if (type == PieceType.BISHOP) {
            return bishopMoves(board, myPosition);
        }

        if (type == PieceType.QUEEN) {
            return queenMoves(board, myPosition);
        }

        if (type == PieceType.PAWN) {
            return pawnMoves(board, myPosition);
        }

        if (type == PieceType.KING) {
            return kingMoves(board, myPosition);
        }

        return knightMoves(board, myPosition);
    }

    private Collection<ChessMove> rookMoves(
            ChessBoard board,
            ChessPosition myPosition) {

        Collection<ChessMove> moves = new ArrayList<>();

        addDirectionalMoves(board, myPosition, moves, 1, 0);
        addDirectionalMoves(board, myPosition, moves, -1, 0);
        addDirectionalMoves(board, myPosition, moves, 0, 1);
        addDirectionalMoves(board, myPosition, moves, 0, -1);

        return moves;
    }

    private Collection<ChessMove> bishopMoves(
            ChessBoard board,
            ChessPosition myPosition) {

        Collection<ChessMove> moves = new ArrayList<>();

        addDirectionalMoves(board, myPosition, moves, 1, 1);
        addDirectionalMoves(board, myPosition, moves, -1, 1);
        addDirectionalMoves(board, myPosition, moves, 1, -1);
        addDirectionalMoves(board, myPosition, moves, -1, -1);

        return moves;
    }

    private Collection<ChessMove> queenMoves(
            ChessBoard board,
            ChessPosition myPosition) {

        Collection<ChessMove> moves = new ArrayList<>();

        addDirectionalMoves(board, myPosition, moves, 1, 0);
        addDirectionalMoves(board, myPosition, moves, -1, 0);
        addDirectionalMoves(board, myPosition, moves, 0, 1);
        addDirectionalMoves(board, myPosition, moves, 0, -1);

        addDirectionalMoves(board, myPosition, moves, 1, 1);
        addDirectionalMoves(board, myPosition, moves, -1, 1);
        addDirectionalMoves(board, myPosition, moves, 1, -1);
        addDirectionalMoves(board, myPosition, moves, -1, -1);

        return moves;
    }

    private void addDirectionalMoves(
            ChessBoard board,
            ChessPosition myPosition,
            Collection<ChessMove> moves,
            int rowChange,
            int colChange) {

        int newRow = myPosition.getRow() + rowChange;
        int newCol = myPosition.getColumn() + colChange;

        while (onBoard(newRow, newCol)) {
            ChessPosition newPosition = new ChessPosition(newRow, newCol);
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

            newRow += rowChange;
            newCol += colChange;
        }
    }

    private Collection<ChessMove> pawnMoves(
            ChessBoard board,
            ChessPosition myPosition) {

        Collection<ChessMove> moves = new ArrayList<>();

        int direction;

        if (pieceColor == ChessGame.TeamColor.WHITE) {
            direction = 1;
        } else {
            direction = -1;
        }

        addPawnForwardMove(board, myPosition, moves, direction);
        addPawnDoubleMove(board, myPosition, moves, direction);
        addPawnCapture(board, myPosition, moves, direction, -1);
        addPawnCapture(board, myPosition, moves, direction, 1);

        return moves;
    }

    private void addPawnForwardMove(
            ChessBoard board,
            ChessPosition myPosition,
            Collection<ChessMove> moves,
            int direction) {

        int newRow = myPosition.getRow() + direction;
        int newCol = myPosition.getColumn();

        if (!onBoard(newRow, newCol)) {
            return;
        }

        ChessPosition newPosition = new ChessPosition(newRow, newCol);

        if (board.getPiece(newPosition) == null) {
            addPawnMove(myPosition, newPosition, moves);
        }
    }

    private void addPawnDoubleMove(
            ChessBoard board,
            ChessPosition myPosition,
            Collection<ChessMove> moves,
            int direction) {

        int row = myPosition.getRow();

        boolean startingRow =
                (pieceColor == ChessGame.TeamColor.WHITE && row == 2)
                        || (pieceColor == ChessGame.TeamColor.BLACK && row == 7);

        if (!startingRow) {
            return;
        }

        ChessPosition firstPosition = new ChessPosition(
                row + direction,
                myPosition.getColumn()
        );

        ChessPosition secondPosition = new ChessPosition(
                row + (direction * 2),
                myPosition.getColumn()
        );

        if (board.getPiece(firstPosition) == null
                && board.getPiece(secondPosition) == null) {

            moves.add(new ChessMove(
                    myPosition,
                    secondPosition,
                    null
            ));
        }
    }

    private void addPawnCapture(
            ChessBoard board,
            ChessPosition myPosition,
            Collection<ChessMove> moves,
            int direction,
            int colChange) {

        int newRow = myPosition.getRow() + direction;
        int newCol = myPosition.getColumn() + colChange;

        if (!onBoard(newRow, newCol)) {
            return;
        }

        ChessPosition newPosition = new ChessPosition(newRow, newCol);
        ChessPiece piece = board.getPiece(newPosition);

        if (piece != null && piece.getTeamColor() != pieceColor) {
            addPawnMove(myPosition, newPosition, moves);
        }
    }

    private void addPawnMove(
            ChessPosition myPosition,
            ChessPosition newPosition,
            Collection<ChessMove> moves) {

        if (promotionRow(newPosition.getRow())) {
            addPromotionMoves(myPosition, newPosition, moves);
        } else {
            moves.add(new ChessMove(
                    myPosition,
                    newPosition,
                    null
            ));
        }
    }

    private boolean promotionRow(int row) {
        return (pieceColor == ChessGame.TeamColor.WHITE && row == 8)
                || (pieceColor == ChessGame.TeamColor.BLACK && row == 1);
    }

    private void addPromotionMoves(
            ChessPosition myPosition,
            ChessPosition newPosition,
            Collection<ChessMove> moves) {

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

    private Collection<ChessMove> kingMoves(
            ChessBoard board,
            ChessPosition myPosition) {

        Collection<ChessMove> moves = new ArrayList<>();

        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        for (int rowChange = -1; rowChange <= 1; rowChange++) {
            for (int colChange = -1; colChange <= 1; colChange++) {

                if (rowChange == 0 && colChange == 0) {
                    continue;
                }

                addSingleMove(
                        board,
                        myPosition,
                        moves,
                        row + rowChange,
                        col + colChange
                );
            }
        }

        return moves;
    }

    private Collection<ChessMove> knightMoves(
            ChessBoard board,
            ChessPosition myPosition) {

        Collection<ChessMove> moves = new ArrayList<>();

        int row = myPosition.getRow();
        int col = myPosition.getColumn();

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
            addSingleMove(
                    board,
                    myPosition,
                    moves,
                    row + change[0],
                    col + change[1]
            );
        }

        return moves;
    }

    private void addSingleMove(
            ChessBoard board,
            ChessPosition myPosition,
            Collection<ChessMove> moves,
            int newRow,
            int newCol) {

        if (!onBoard(newRow, newCol)) {
            return;
        }

        ChessPosition newPosition = new ChessPosition(newRow, newCol);
        ChessPiece piece = board.getPiece(newPosition);

        if (piece == null || piece.getTeamColor() != pieceColor) {
            moves.add(new ChessMove(
                    myPosition,
                    newPosition,
                    null
            ));
        }
    }

    private boolean onBoard(int row, int col) {
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }
}