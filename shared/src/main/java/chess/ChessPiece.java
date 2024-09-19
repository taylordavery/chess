package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor TeamColor;
    private final ChessPiece.PieceType PieceType;
    private boolean justDoubleMoved;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.PieceType = type;
        this.TeamColor = pieceColor;
        this.justDoubleMoved = false;
    }

    @Override
    public String toString() {
        if (this.PieceType != null) {
            return this.TeamColor.toString() + " " + this.PieceType.toString();
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return this.TeamColor.equals(that.TeamColor) && this.PieceType.equals(that.PieceType);
    }

    @Override
    public int hashCode() {
        int result = this.TeamColor.hashCode();
        result = 31 * result + this.PieceType.hashCode();
        return result;
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
        return this.TeamColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.PieceType;
    }

    public boolean getJustDoubleMoved() {
        return this.justDoubleMoved;
    }

    public void setJustDoubleMoved(boolean setting) {
        this.justDoubleMoved = setting;
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

        if (this.PieceType == PieceType.KING || this.PieceType == PieceType.QUEEN || this.PieceType == PieceType.BISHOP || this.PieceType == PieceType.ROOK || this.PieceType == PieceType.KNIGHT) {
            var i = myPosition;

            int[][] tuples;

            switch (this.PieceType) {
                case BISHOP ->
                        tuples = new int[][]{{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
                case QUEEN, KING ->
                        tuples = new int[][]{{1, 1}, {1, -1}, {-1, 1}, {-1, -1}, {1, 0}, {-1, 0}, {0, 1}, {0, -1}};
                case ROOK ->
                        tuples = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
                case KNIGHT ->
                        tuples = new int[][]{{1, 2}, {1, -2}, {-1, 2}, {-1, -2}, {2, 1}, {2, -1}, {-2, 1}, {-2, -1}};
                default ->
                        throw new IllegalArgumentException("Unknown piece type: " + this.PieceType);
            }

            for (int[] tuple : tuples) {
                i = myPosition;
                ChessMove j;
                while (i.getRow() > 0 && i.getRow() < 9 && i.getColumn() > 0 && i.getColumn() < 9) {
                    i = new ChessPosition(i.getRow() + tuple[0], i.getColumn() + tuple[1]);

                    if (i.getRow() < 1 || i.getRow() > 8 || i.getColumn() < 1 || i.getColumn() > 8) {
                        break;
                    }

                    j = new ChessMove(myPosition, i, null);

                    ChessPiece targetPiece = board.getPiece(i);

                    if (targetPiece != null) {
                        if (targetPiece.getTeamColor() != this.getTeamColor()) {
                            moves.add(j);
                        }
                        break;
                    } else moves.add(j);

                    if (this.PieceType == PieceType.KING || this.PieceType == PieceType.KNIGHT) break;
                }
            }
        }

        if (this.PieceType == PieceType.PAWN) {
            int direction;
            if (this.TeamColor == ChessGame.TeamColor.BLACK) {
                direction = -1;
            } else {
                direction = 1;
            }

            // Move forward one space
            ChessPosition i = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn());
            if (board.getPiece(i) == null && i.getRow() != 8 && i.getRow() != 1) {
                moves.add(new ChessMove(myPosition, i, null));
                i = new ChessPosition(i.getRow() + direction, i.getColumn());
                if (board.getPiece(i) == null && ((i.getRow() == 4 && this.getTeamColor() == ChessGame.TeamColor.WHITE) || (i.getRow() == 5 && this.getTeamColor() == ChessGame.TeamColor.BLACK))) {
                    moves.add(new ChessMove(myPosition, i, null));
                }
            } else if (board.getPiece(i) == null && (i.getRow() == 8 || i.getRow() == 1)) {
                moves.add(new ChessMove(myPosition, i, PieceType.QUEEN));
                moves.add(new ChessMove(myPosition, i, PieceType.ROOK));
                moves.add(new ChessMove(myPosition, i, PieceType.BISHOP));
                moves.add(new ChessMove(myPosition, i, PieceType.KNIGHT));
            }

            i = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn() - 1);
            if (board.getPiece(i) != null && board.getPiece(i).getTeamColor() != this.getTeamColor()) {
                if (i.getRow() == 1 || i.getRow() == 8) {
                    moves.add(new ChessMove(myPosition, i, PieceType.QUEEN));
                    moves.add(new ChessMove(myPosition, i, PieceType.ROOK));
                    moves.add(new ChessMove(myPosition, i, PieceType.BISHOP));
                    moves.add(new ChessMove(myPosition, i, PieceType.KNIGHT));
                } else {
                    moves.add(new ChessMove(myPosition, i, null));
                }
            }

            i = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn() + 1);
            if (board.getPiece(i) != null && board.getPiece(i).getTeamColor() != this.getTeamColor()) {
                if (i.getRow() == 1 || i.getRow() == 8) {
                    moves.add(new ChessMove(myPosition, i, PieceType.QUEEN));
                    moves.add(new ChessMove(myPosition, i, PieceType.ROOK));
                    moves.add(new ChessMove(myPosition, i, PieceType.BISHOP));
                    moves.add(new ChessMove(myPosition, i, PieceType.KNIGHT));
                } else {
                    moves.add(new ChessMove(myPosition, i, null));
                }
            }

            // en passant
            i = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn() - 1);
            ChessPiece passingEnemyPiece = board.getPiece(new ChessPosition(myPosition.getRow(), myPosition.getColumn() - 1));
            if (passingEnemyPiece != null && passingEnemyPiece.getJustDoubleMoved() && passingEnemyPiece.getTeamColor() != this.getTeamColor()) {
                ChessMove enPassantMove = new ChessMove(myPosition, i, null);
                enPassantMove.setIsEnPassantMove(true);
                moves.add(enPassantMove);
            }

            i = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn() + 1);
            passingEnemyPiece = board.getPiece(new ChessPosition(myPosition.getRow(), myPosition.getColumn() + 1));
            if (passingEnemyPiece != null && passingEnemyPiece.getJustDoubleMoved() && passingEnemyPiece.getTeamColor() != this.getTeamColor()) {
                ChessMove enPassantMove = new ChessMove(myPosition, i, null);
                enPassantMove.setIsEnPassantMove(true);
                moves.add(enPassantMove);
            }



        }
        return moves;
    }
}