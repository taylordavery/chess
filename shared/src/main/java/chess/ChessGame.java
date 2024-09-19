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
        if (this.board.getPiece(startPosition) == null) {return null;}
        return this.board.getPiece(startPosition).pieceMoves(this.board, startPosition);
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece from_piece = this.board.getPiece(move.getStartPosition());
        ChessPiece to_piece = this.board.getPiece(move.getEndPosition());

        if (from_piece == null) {
            throw new InvalidMoveException("No piece in starting position");
        }

         for (ChessMove validMove : this.validMoves(move.getStartPosition())) {
             if (move.equals(validMove)) {
                 this.board.squares[move.getStartPosition().getRow()-1][move.getStartPosition().getColumn()-1] = null;
                 this.board.squares[move.getEndPosition().getRow()-1][move.getEndPosition().getColumn()-1] = from_piece;
                 if (isInCheck(from_piece.getTeamColor())) {
                     this.board.squares[move.getStartPosition().getRow()-1][move.getStartPosition().getColumn()-1] = from_piece;
                     this.board.squares[move.getEndPosition().getRow()-1][move.getEndPosition().getColumn()-1] = to_piece;
                     throw new InvalidMoveException("Can't be in check after your move");
                 } else {
                     if (move.getPromotionPiece() != null) {
                         this.board.squares[move.getEndPosition().getRow()-1][move.getEndPosition().getColumn()-1] = new ChessPiece(from_piece.getTeamColor(), move.getPromotionPiece());
                     }
                 }
                 return;
             }
         }

         throw new InvalidMoveException("Not a valid move");

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        int rowNum = 0;
        int colNum;
        ChessPosition kingPosition = null;

        for (ChessPiece[] row : this.board.squares) {
            rowNum = rowNum + 1;
            colNum = 0;
            for (ChessPiece piece : row) {
                colNum = colNum + 1;
                if (piece != null && piece.getTeamColor().equals(teamColor)) {
                    if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                        kingPosition = new ChessPosition(rowNum, colNum);
                    }
                }
            }
        }

        rowNum = 0;
        for (ChessPiece[] row : this.board.squares) {
            rowNum = rowNum + 1;
            colNum = 0;
            for (ChessPiece piece : row) {
                colNum = colNum + 1;
                if (piece != null && !piece.getTeamColor().equals(teamColor)) {
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
        int rowNum = 0;
        int colNum;

        for (ChessPiece[] row : this.board.squares) {
            rowNum = rowNum + 1;
            colNum = 0;
            for (ChessPiece piece : row) {
                colNum = colNum + 1;
                if (piece != null && piece.getTeamColor().equals(teamColor)) {
                    for (ChessMove move : this.validMoves(new ChessPosition(rowNum, colNum))) {
                        return false;
                    }
                }
            }
        }
        return true;
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
