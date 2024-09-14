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

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.PieceType = type;
        this.TeamColor = pieceColor;
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

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        var moves = new ArrayList<ChessMove>();
        var i = myPosition;
        int[][] tuples = null;

        if (this.PieceType == PieceType.BISHOP) {
            tuples = new int[][] {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        }

        if (this.PieceType == PieceType.QUEEN || this.PieceType == PieceType.KING) {
            tuples = new int[][] {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}, {1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        }

        if (this.PieceType == PieceType.ROOK) {
            tuples = new int[][] {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        }

        if (tuples != null) {
            for (int[] tuple : tuples) {
                i = myPosition;
                ChessMove j;
                while (i.getRow() > 0 && i.getRow() < 9 && i.getColumn() > 0 && i.getColumn() < 9) {
                    i = new ChessPosition(i.getRow() + tuple[0], i.getColumn() + tuple[1]);

                    if (i.getRow() < 1 || i.getRow() > 8 || i.getColumn() < 1 || i.getColumn() > 8) {
                        break;
                    }

                    j = new ChessMove(myPosition, i, this.getPieceType());

                    ChessPiece targetPiece = board.getPiece(i);

                    if (targetPiece != null) {
                        if (targetPiece.getTeamColor() != this.getTeamColor() && targetPiece.getPieceType() != PieceType.KING) {
                            moves.add(j);
                        }
                        break;
                    } else moves.add(j);

                    if (this.PieceType == PieceType.KING) break;
                }
            }
        }
        return moves;
    }
}