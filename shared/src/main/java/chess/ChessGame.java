package chess;

import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private final ChessBoard board;
    private ChessGame.TeamColor TeamTurn;

    public ChessGame() {
        this.board = new ChessBoard();
        this.TeamTurn = ChessGame.TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.TeamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.TeamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        return this.board.getPiece(startPosition).pieceMoves(this.board, startPosition);
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = this.board.getPiece(move.getStartPosition());

        if (piece == null) {
            throw new InvalidMoveException("No piece in starting position");
        }

         for (ChessMove validMove : validMoves(move.getStartPosition())) {
             if (move.equals(validMove)) {
                 this.board.squares[move.getStartPosition().getRow()][move.getStartPosition().getColumn()] = null;
                 this.board.squares[move.getStartPosition().getRow()][move.getStartPosition().getColumn()] = piece;
             } else {
                 throw new InvalidMoveException("Not a valid move");
             }
         }

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        int rowNum = 0;
        int colNum = 0;
        ChessPosition kingPosition = null;

        for (ChessPiece[] row : this.board.squares) {
            rowNum = rowNum + 1;
            for (ChessPiece piece : row) {
                colNum = colNum + 1;
                if (piece.getTeamColor().equals(teamColor)) {
                    if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                        kingPosition = new ChessPosition(rowNum, colNum);
                    }
                }
            }
        }

        rowNum = 0;
        colNum = 0;
        for (ChessPiece[] row : this.board.squares) {
            rowNum = rowNum + 1;
            for (ChessPiece piece : row) {
                colNum = colNum + 1;
                if (!piece.getTeamColor().equals(teamColor)) {
                    for (ChessMove move : piece.pieceMoves(this.board, new ChessPosition(rowNum, colNum))) {
                        if (move.getEndPosition().equals(kingPosition)) {
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
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                this.board.squares[i][j] = board.squares[i][j];
            }
        }
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }
}
