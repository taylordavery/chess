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

        if (this.PieceType == PieceType.BISHOP) {
            var i = myPosition;
            while (i.getRow() > 1 && i.getRow() < 8 && i.getColumn() > 1 && i.getColumn() < 8) {
                i = new ChessPosition(i.getRow() + 1, i.getColumn() - 1);
                var j = new ChessMove(myPosition, i, this.getPieceType());
                if (board.getPiece(i) != null) {
                    if (board.getPiece(i).getTeamColor() != this.getTeamColor() && board.getPiece(i).getPieceType() != PieceType.KING) {
                        moves.add(j);
                    }
                    break;
                }
                moves.add(j);
            }

            i = myPosition;
            while (i.getRow() > 1 && i.getRow() < 8 && i.getColumn() > 1 && i.getColumn() < 8) {
                i = new ChessPosition(i.getRow() - 1, i.getColumn() - 1);
                var j = new ChessMove(myPosition, i, this.getPieceType());
                if (board.getPiece(i) != null) {
                    if (board.getPiece(i).getTeamColor() != this.getTeamColor() && board.getPiece(i).getPieceType() != PieceType.KING) {
                        moves.add(j);
                    }
                    break;
                }
                moves.add(j);
            }

            i = myPosition;
            while (i.getRow() > 1 && i.getRow() < 8 && i.getColumn() > 1 && i.getColumn() < 8) {
                i = new ChessPosition(i.getRow() + 1, i.getColumn() + 1);
                var j = new ChessMove(myPosition, i, this.getPieceType());
                if (board.getPiece(i) != null) {
                    if (board.getPiece(i).getTeamColor() != this.getTeamColor() && board.getPiece(i).getPieceType() != PieceType.KING) {
                        moves.add(j);
                    }
                    break;
                }
                moves.add(j);
            }

            i = myPosition;
            while (i.getRow() > 1 && i.getRow() < 8 && i.getColumn() > 1 && i.getColumn() < 8) {
                i = new ChessPosition(i.getRow() - 1, i.getColumn() + 1);
                var j = new ChessMove(myPosition, i, this.getPieceType());
                if (board.getPiece(i) != null) {
                    if (board.getPiece(i).getTeamColor() != this.getTeamColor() && board.getPiece(i).getPieceType() != PieceType.KING) {
                        moves.add(j);
                    }
                    break;
                }
                moves.add(j);
            }
        }

        if (this.PieceType == PieceType.KING) {
            var i = myPosition;
            if (i.getRow() > 1 && i.getRow() < 8 && i.getColumn() > 1 && i.getColumn() < 8) {
                i = new ChessPosition(i.getRow() + 1, i.getColumn() - 1);
                var j = new ChessMove(myPosition, i, this.getPieceType());
                if (board.getPiece(i) != null) {
                    if (board.getPiece(i).getTeamColor() != this.getTeamColor() && board.getPiece(i).getPieceType() != PieceType.KING) {
                        moves.add(j);
                    }
                }
                moves.add(j);
            }

            i = myPosition;
            if (i.getRow() > 1 && i.getRow() < 8 && i.getColumn() > 1 && i.getColumn() < 8) {
                i = new ChessPosition(i.getRow() - 1, i.getColumn() - 1);
                var j = new ChessMove(myPosition, i, this.getPieceType());
                if (board.getPiece(i) != null) {
                    if (board.getPiece(i).getTeamColor() != this.getTeamColor() && board.getPiece(i).getPieceType() != PieceType.KING) {
                        moves.add(j);
                    }
                }
                moves.add(j);
            }

            i = myPosition;
            if (i.getRow() > 1 && i.getRow() < 8 && i.getColumn() > 1 && i.getColumn() < 8) {
                i = new ChessPosition(i.getRow() + 1, i.getColumn() + 1);
                var j = new ChessMove(myPosition, i, this.getPieceType());
                if (board.getPiece(i) != null) {
                    if (board.getPiece(i).getTeamColor() != this.getTeamColor() && board.getPiece(i).getPieceType() != PieceType.KING) {
                        moves.add(j);
                    }
                }
                moves.add(j);
            }

            i = myPosition;
            if (i.getRow() > 1 && i.getRow() < 8 && i.getColumn() > 1 && i.getColumn() < 8) {
                i = new ChessPosition(i.getRow() - 1, i.getColumn() + 1);
                var j = new ChessMove(myPosition, i, this.getPieceType());
                if (board.getPiece(i) != null) {
                    if (board.getPiece(i).getTeamColor() != this.getTeamColor() && board.getPiece(i).getPieceType() != PieceType.KING) {
                        moves.add(j);
                    }
                }
                moves.add(j);
            }

            i = myPosition;
            if (i.getRow() > 1 && i.getRow() < 8 && i.getColumn() > 1 && i.getColumn() < 8) {
                i = new ChessPosition(i.getRow(), i.getColumn() - 1);
                var j = new ChessMove(myPosition, i, this.getPieceType());
                if (board.getPiece(i) != null) {
                    if (board.getPiece(i).getTeamColor() != this.getTeamColor() && board.getPiece(i).getPieceType() != PieceType.KING) {
                        moves.add(j);
                    }
                }
                moves.add(j);
            }

            i = myPosition;
            if (i.getRow() > 1 && i.getRow() < 8 && i.getColumn() > 1 && i.getColumn() < 8) {
                i = new ChessPosition(i.getRow() - 1, i.getColumn());
                var j = new ChessMove(myPosition, i, this.getPieceType());
                if (board.getPiece(i) != null) {
                    if (board.getPiece(i).getTeamColor() != this.getTeamColor() && board.getPiece(i).getPieceType() != PieceType.KING) {
                        moves.add(j);
                    }
                }
                moves.add(j);
            }

            i = myPosition;
            if (i.getRow() > 1 && i.getRow() < 8 && i.getColumn() > 1 && i.getColumn() < 8) {
                i = new ChessPosition(i.getRow(), i.getColumn() + 1);
                var j = new ChessMove(myPosition, i, this.getPieceType());
                if (board.getPiece(i) != null) {
                    if (board.getPiece(i).getTeamColor() != this.getTeamColor() && board.getPiece(i).getPieceType() != PieceType.KING) {
                        moves.add(j);
                    }
                }
                moves.add(j);
            }

            i = myPosition;
            if (i.getRow() > 1 && i.getRow() < 8 && i.getColumn() > 1 && i.getColumn() < 8) {
                i = new ChessPosition(i.getRow() - 1, i.getColumn());
                var j = new ChessMove(myPosition, i, this.getPieceType());
                if (board.getPiece(i) != null) {
                    if (board.getPiece(i).getTeamColor() != this.getTeamColor() && board.getPiece(i).getPieceType() != PieceType.KING) {
                        moves.add(j);
                    }
                }
                moves.add(j);
            }
        }

        return moves;
    }

}
