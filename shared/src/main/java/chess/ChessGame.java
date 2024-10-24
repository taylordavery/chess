package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private final ChessBoard board;
    private ChessGame.TeamColor teamTurn;

    public ChessGame() {
        this.board = new ChessBoard();
        this.board.resetBoard();
        this.teamTurn = ChessGame.TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
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

        ArrayList<ChessMove> validMoves = new ArrayList<>();

        ChessPiece fromPiece = this.board.getPiece(startPosition);
        ChessPiece toPiece = null;

        for (ChessMove move : this.board.getPiece(startPosition).pieceMoves(this.board, startPosition)) {

            if (move.getIsCastleMove()) {
                if (!isInCheck(fromPiece.getTeamColor())) {
                    // left castle
                    leftCastle(move, fromPiece, validMoves);

                    // right castle
                    rightCastle(move, fromPiece, validMoves);
                }
            } else {
                toPiece = this.board.squares[(move.getEndPosition()).getRow() - 1][(move.getEndPosition()).getColumn() - 1];
                this.board.squares[move.getStartPosition().getRow() - 1][move.getStartPosition().getColumn() - 1] = null;
                this.board.squares[move.getEndPosition().getRow() - 1][move.getEndPosition().getColumn() - 1] = fromPiece;

                if (isInCheck(fromPiece.getTeamColor())) {
                    this.board.squares[move.getStartPosition().getRow() - 1][move.getStartPosition().getColumn() - 1] = fromPiece;
                    this.board.squares[move.getEndPosition().getRow() - 1][move.getEndPosition().getColumn() - 1] = toPiece;
                } else {
                    validMoves.add(move);
                    this.board.squares[move.getStartPosition().getRow() - 1][move.getStartPosition().getColumn() - 1] = fromPiece;
                    this.board.squares[move.getEndPosition().getRow() - 1][move.getEndPosition().getColumn() - 1] = toPiece;
                }
            }
        }
        return validMoves;
    }

    private void rightCastle(ChessMove move, ChessPiece fromPiece, ArrayList<ChessMove> validMoves) {
        ChessPiece toPiece;
        if (move.getEndPosition().getColumn() > move.getStartPosition().getColumn()) {
            toPiece = this.board.squares[(move.getStartPosition()).getRow()-1][(move.getStartPosition().getColumn())];
            this.board.squares[move.getStartPosition().getRow()-1][move.getStartPosition().getColumn()-1] = null;
            this.board.squares[move.getStartPosition().getRow()-1][move.getStartPosition().getColumn()] = fromPiece;
            if (isInCheck(fromPiece.getTeamColor())) {
                this.board.squares[move.getStartPosition().getRow() - 1][move.getStartPosition().getColumn() - 1] = fromPiece;
                this.board.squares[(move.getStartPosition()).getRow()-1][(move.getStartPosition().getColumn())] = toPiece;
            } else {
                toPiece = this.board.squares[(move.getStartPosition()).getRow()-1][(move.getStartPosition().getColumn()+1)];
                this.board.squares[move.getStartPosition().getRow()-1][move.getStartPosition().getColumn()] = null;
                this.board.squares[move.getStartPosition().getRow()-1][move.getStartPosition().getColumn()+1] = fromPiece;
                if (!isInCheck(fromPiece.getTeamColor())) {
                    validMoves.add(move);
                }
                this.board.squares[move.getStartPosition().getRow() - 1][move.getStartPosition().getColumn() - 1] = fromPiece;
                this.board.squares[(move.getStartPosition()).getRow() - 1][(move.getStartPosition().getColumn() + 1)] = toPiece;
            }
        }
    }

    private void leftCastle(ChessMove move, ChessPiece fromPiece, ArrayList<ChessMove> validMoves) {
        ChessPiece toPiece;
        if (move.getEndPosition().getColumn() < move.getStartPosition().getColumn()) {
            toPiece = this.board.squares[(move.getStartPosition()).getRow()-1][(move.getStartPosition().getColumn()-2)];
            this.board.squares[move.getStartPosition().getRow()-1][move.getStartPosition().getColumn()-1] = null;
            this.board.squares[move.getStartPosition().getRow()-1][move.getStartPosition().getColumn()-2] = fromPiece;
            if (isInCheck(fromPiece.getTeamColor())) {
                this.board.squares[move.getStartPosition().getRow() - 1][move.getStartPosition().getColumn() - 1] = fromPiece;
                this.board.squares[(move.getStartPosition()).getRow()-1][(move.getStartPosition().getColumn()-2)] = toPiece;
            } else {
                toPiece = this.board.squares[(move.getStartPosition()).getRow()-1][(move.getStartPosition().getColumn()-3)];
                this.board.squares[move.getStartPosition().getRow()-1][move.getStartPosition().getColumn()-2] = null;
                this.board.squares[move.getStartPosition().getRow()-1][move.getStartPosition().getColumn()-3] = fromPiece;
                if (isInCheck(fromPiece.getTeamColor())) {
                    this.board.squares[move.getStartPosition().getRow() - 1][move.getStartPosition().getColumn() - 1] = fromPiece;
                    this.board.squares[(move.getStartPosition()).getRow() - 1][(move.getStartPosition().getColumn() - 3)] = toPiece;
                } else {
                    validMoves.add(move);
                    this.board.squares[move.getStartPosition().getRow() - 1][move.getStartPosition().getColumn() - 1] = fromPiece;
                    this.board.squares[(move.getStartPosition()).getRow() - 1][(move.getStartPosition().getColumn() - 3)] = toPiece;
                }
            }
        }
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece fromPiece = this.board.getPiece(move.getStartPosition());
        int endPosCol = move.getEndPosition().getColumn() - 1;
        ChessPiece toPiece = this.board.squares[(move.getEndPosition()).getRow()-1][endPosCol];

        if (fromPiece == null) {
            throw new InvalidMoveException("No piece in starting position");
        }


        int rowNum = 0;
        int colNum;

        for (ChessPiece[] row : this.board.squares) {
            rowNum = rowNum + 1;
            colNum = 0;
            for (ChessPiece piece : row) {
                colNum = colNum + 1;
                if (piece != null && piece.getTeamColor().equals(fromPiece.getTeamColor())) {
                    piece.setJustDoubleMoved(false);
                }
            }
        }


        if (fromPiece.getTeamColor() != this.getTeamTurn()) {
            throw new InvalidMoveException("Not your turn");
        }

         for (ChessMove validMove : this.validMoves(move.getStartPosition())) {
             if (move.equals(validMove)) {
                 this.board.squares[move.getStartPosition().getRow()-1][move.getStartPosition().getColumn()-1] = null;
                 int endPosRow = move.getEndPosition().getRow() - 1;
                 this.board.squares[endPosRow][endPosCol] = fromPiece;

                 this.board.squares[endPosRow][endPosCol].setHasMoved(true);

                 if (validMove.getIsCastleMove()) {
                     // left castle
                     if (validMove.getEndPosition().getColumn() < validMove.getStartPosition().getColumn()) {
                         ChessPiece rook = this.board.squares[validMove.getStartPosition().getRow()-1][0];
                         this.board.squares[validMove.getStartPosition().getRow()-1][0] = null;
                         this.board.squares[validMove.getEndPosition().getRow()-1][3] = rook;
                     }

                     // right castle
                     if (validMove.getEndPosition().getColumn() > validMove.getStartPosition().getColumn()) {
                         ChessPiece rook = this.board.squares[validMove.getStartPosition().getRow()-1][7];
                         this.board.squares[validMove.getStartPosition().getRow()-1][7] = null;
                         this.board.squares[validMove.getEndPosition().getRow()-1][5] = rook;
                     }
                 }

                 if (validMove.getIsEnPassantMove()) {
                     this.board.squares[move.getStartPosition().getRow()-1][endPosCol] = null;
                 }

                 if (move.getPromotionPiece() != null) {
                     this.board.squares[endPosRow][endPosCol] =
                             new ChessPiece(fromPiece.getTeamColor(), move.getPromotionPiece());
                 }


                 if (this.getTeamTurn() == ChessGame.TeamColor.WHITE) {
                     this.setTeamTurn(ChessGame.TeamColor.BLACK);
                 } else if (this.getTeamTurn() == ChessGame.TeamColor.BLACK) {
                     this.setTeamTurn(ChessGame.TeamColor.WHITE);
                 }

                 if (this.board.squares[endPosRow][endPosCol].getPieceType() == ChessPiece.PieceType.PAWN) {
                     if (move.getStartPosition().getRow() - move.getEndPosition().getRow() > 1 ||
                             move.getStartPosition().getRow() - move.getEndPosition().getRow() < -1) {
                         this.board.squares[endPosRow][endPosCol].setJustDoubleMoved(true);
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
                    if (checkMovesEndOnKing(piece, rowNum, colNum, kingPosition)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean checkMovesEndOnKing(ChessPiece piece, int rowNum, int colNum, ChessPosition kingPosition) {
        for (ChessMove move : piece.pieceMoves(this.board, new ChessPosition(rowNum, colNum))) {
            if (move.getEndPosition().equals(kingPosition)) {
                return true;
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
        return this.isInCheck(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
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
        return !this.isInCheck(teamColor);
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
}//
