package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {

    private final ChessPosition startPosition;
    private final ChessPosition endPosition;
    private final ChessPiece.PieceType promotionPiece;
    private boolean isCastleMove;
    private boolean isEnPassantMove;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
        this.isEnPassantMove = false;
        this.isCastleMove = false;
    }

    public boolean getIsEnPassantMove() {
        return this.isEnPassantMove;
    }

    public void setIsEnPassantMove(boolean setting) {
        this.isEnPassantMove = setting;
    }

    public boolean getIsCastleMove() {
        return this.isCastleMove;
    }

    public void setIsCastleMove(boolean setting) {
        this.isCastleMove = setting;
    }

    @Override
    public String toString() {
        return startPosition + " -> " + endPosition + " (" + promotionPiece + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessMove that = (ChessMove) o;
        if (startPosition.equals(that.startPosition) && endPosition.equals(that.endPosition)) {
            if (promotionPiece == null && that.promotionPiece == null) {
                return true;
            }
            if (promotionPiece == null) {
                return false;
            }
            return promotionPiece.equals(that.promotionPiece);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = startPosition.hashCode();
        result = 31 * result + endPosition.hashCode();
        if (promotionPiece != null) {
            result = 31 * result + promotionPiece.hashCode();
        }
        return result;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return startPosition;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return endPosition;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return promotionPiece;
    }
}//