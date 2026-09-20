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
        return Objects.equals(board, chessGame.board) && teamTurn == chessGame.teamTurn && Objects.equals(lastMove, chessGame.lastMove);
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

            ChessPosition endPosition = move.getEndPosition();

            ChessPiece capturedPiece = board.getPiece(endPosition);

            // Remove piece from starting position
            board.addPiece(startPosition, null);

            // Check if this move is a promotion
            if (move.getPromotionPiece() != null) {
                board.addPiece(
                        endPosition,
                        new ChessPiece(
                                piece.getTeamColor(),
                                move.getPromotionPiece()
                        )
                );
            }
            else {
                board.addPiece(endPosition, piece);
            }

            // If our king is not in check after the move,
            // then the move is valid
            if (!isInCheck(piece.getTeamColor())) {
                validMoves.add(move);
            }

            // Put the board back how it was
            board.addPiece(startPosition, piece);
            board.addPiece(endPosition, capturedPiece);
        }

        return validMoves;
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

        // Make sure there is actually a piece there
        if (piece == null) {
            throw new InvalidMoveException("There is no piece at the starting position");
        }

        // Make sure it is this team's turn
        if (piece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException("It is not this team's turn");
        }

        Collection<ChessMove> moves = validMoves(startPosition);

        boolean validMove = false;

        for (ChessMove possibleMove : moves) {

            ChessPosition possibleStart = possibleMove.getStartPosition();
            ChessPosition possibleEnd = possibleMove.getEndPosition();

            boolean sameStart =
                    possibleStart.getRow() == startPosition.getRow() &&
                            possibleStart.getColumn() == startPosition.getColumn();

            boolean sameEnd =
                    possibleEnd.getRow() == endPosition.getRow() &&
                            possibleEnd.getColumn() == endPosition.getColumn();

            boolean samePromotion =
                    possibleMove.getPromotionPiece() == move.getPromotionPiece();

            if (sameStart && sameEnd && samePromotion) {
                validMove = true;
                break;
            }
        }

        if (!validMove) {
            throw new InvalidMoveException("That move is not valid");
        }

        // Remove the piece from its old position
        board.addPiece(startPosition, null);

        // Promotion
        if (move.getPromotionPiece() != null) {
            board.addPiece(
                    endPosition,
                    new ChessPiece(
                            piece.getTeamColor(),
                            move.getPromotionPiece()
                    )
            );
        }
        else {
            board.addPiece(endPosition, piece);
        }

        // Save this move for things such as en passant later
        lastMove = move;

        // Change turns
        if (teamTurn == TeamColor.WHITE) {
            teamTurn = TeamColor.BLACK;
        }
        else {
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

        ChessPosition kingPosition = null;

        // Find this team's king
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {

                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);

                if (piece != null &&
                        piece.getTeamColor() == teamColor &&
                        piece.getPieceType() == ChessPiece.PieceType.KING) {

                    kingPosition = position;
                    break;
                }
            }

            if (kingPosition != null) {
                break;
            }
        }

        if (kingPosition == null) {
            return false;
        }

        // Look through every enemy piece
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {

                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);

                if (piece != null && piece.getTeamColor() != teamColor) {

                    Collection<ChessMove> enemyMoves =
                            piece.pieceMoves(board, position);

                    for (ChessMove move : enemyMoves) {

                        ChessPosition endPosition = move.getEndPosition();

                        if (endPosition.getRow() == kingPosition.getRow() &&
                                endPosition.getColumn() == kingPosition.getColumn()) {

                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {

        // Can't be checkmate if they aren't in check
        if (!isInCheck(teamColor)) {
            return false;
        }

        // Look for any piece that still has a valid move
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {

                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);

                if (piece != null && piece.getTeamColor() == teamColor) {

                    Collection<ChessMove> moves = validMoves(position);

                    if (moves != null && !moves.isEmpty()) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {

        // If they are in check, it is not stalemate
        if (isInCheck(teamColor)) {
            return false;
        }

        // Look for any piece that still has a valid move
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {

                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);

                if (piece != null && piece.getTeamColor() == teamColor) {

                    Collection<ChessMove> moves = validMoves(position);

                    if (moves != null && !moves.isEmpty()) {
                        return false;
                    }
                }
            }
        }

        return true;
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