package chess;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Objects;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ChessGame chessGame = (ChessGame) o;

        return Objects.equals(board, chessGame.board)
                && teamTurn == chessGame.teamTurn
                && Objects.equals(lastMove, chessGame.lastMove);
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, teamTurn, lastMove);
    }

    private ChessBoard board;
    private TeamColor teamTurn;
    private ChessMove lastMove;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();

        teamTurn = TeamColor.WHITE;
        lastMove = null;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {

        ChessPiece piece = board.getPiece(startPosition);

        if (piece == null) {
            return null;
        }

        Collection<ChessMove> possibleMoves =
                piece.pieceMoves(board, startPosition);

        Collection<ChessMove> validMoves = new ArrayList<>();

        for (ChessMove move : possibleMoves) {
            if (moveKeepsKingSafe(piece, startPosition, move)) {
                validMoves.add(move);
            }
        }

        return validMoves;
    }

    private boolean moveKeepsKingSafe(
            ChessPiece piece,
            ChessPosition startPosition,
            ChessMove move) {

        ChessPosition endPosition = move.getEndPosition();
        ChessPiece capturedPiece = board.getPiece(endPosition);

        board.addPiece(startPosition, null);
        placeMovedPiece(piece, move);

        boolean safe = !isInCheck(piece.getTeamColor());

        board.addPiece(startPosition, piece);
        board.addPiece(endPosition, capturedPiece);

        return safe;
    }

    private void placeMovedPiece(
            ChessPiece piece,
            ChessMove move) {

        ChessPosition endPosition = move.getEndPosition();

        if (move.getPromotionPiece() != null) {
            board.addPiece(
                    endPosition,
                    new ChessPiece(
                            piece.getTeamColor(),
                            move.getPromotionPiece()
                    )
            );
        } else {
            board.addPiece(endPosition, piece);
        }
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {

        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();

        ChessPiece piece = board.getPiece(startPosition);

        if (piece == null) {
            throw new InvalidMoveException(
                    "There is no piece at the starting position"
            );
        }

        if (piece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException(
                    "It is not this team's turn"
            );
        }

        Collection<ChessMove> moves = validMoves(startPosition);

        if (!moves.contains(move)) {
            throw new InvalidMoveException(
                    "That move is not valid"
            );
        }

        board.addPiece(startPosition, null);
        placeMovedPiece(piece, move);

        lastMove = move;
        changeTurn();
    }

    private void changeTurn() {
        if (teamTurn == TeamColor.WHITE) {
            teamTurn = TeamColor.BLACK;
        } else {
            teamTurn = TeamColor.WHITE;
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {

        ChessPosition kingPosition = findKing(teamColor);

        if (kingPosition == null) {
            return false;
        }

        return enemyCanReachKing(teamColor, kingPosition);
    }

    private ChessPosition findKing(TeamColor teamColor) {

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {

                ChessPosition position =
                        new ChessPosition(row, col);

                ChessPiece piece =
                        board.getPiece(position);

                if (isKingForTeam(piece, teamColor)) {
                    return position;
                }
            }
        }

        return null;
    }

    private boolean isKingForTeam(
            ChessPiece piece,
            TeamColor teamColor) {

        return piece != null
                && piece.getTeamColor() == teamColor
                && piece.getPieceType()
                == ChessPiece.PieceType.KING;
    }

    private boolean enemyCanReachKing(
            TeamColor teamColor,
            ChessPosition kingPosition) {

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {

                ChessPosition position =
                        new ChessPosition(row, col);

                ChessPiece piece =
                        board.getPiece(position);

                if (piece == null
                        || piece.getTeamColor() == teamColor) {
                    continue;
                }

                if (pieceCanReach(
                        piece,
                        position,
                        kingPosition)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean pieceCanReach(
            ChessPiece piece,
            ChessPosition startPosition,
            ChessPosition targetPosition) {

        Collection<ChessMove> moves =
                piece.pieceMoves(board, startPosition);

        for (ChessMove move : moves) {
            if (samePosition(
                    move.getEndPosition(),
                    targetPosition)) {
                return true;
            }
        }

        return false;
    }

    private boolean samePosition(
            ChessPosition first,
            ChessPosition second) {

        return first.getRow() == second.getRow()
                && first.getColumn() == second.getColumn();
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {

        if (!isInCheck(teamColor)) {
            return false;
        }

        return !teamHasValidMove(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {

        if (isInCheck(teamColor)) {
            return false;
        }

        return !teamHasValidMove(teamColor);
    }

    private boolean teamHasValidMove(TeamColor teamColor) {

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {

                ChessPosition position =
                        new ChessPosition(row, col);

                ChessPiece piece =
                        board.getPiece(position);

                if (piece == null
                        || piece.getTeamColor() != teamColor) {
                    continue;
                }

                Collection<ChessMove> moves =
                        validMoves(position);

                if (moves != null && !moves.isEmpty()) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}