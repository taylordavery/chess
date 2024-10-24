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

    private final ChessGame.TeamColor teamColor;
    private final ChessPiece.PieceType pieceType;
    private boolean hasMoved;
    private boolean justDoubleMoved;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceType = type;
        this.teamColor = pieceColor;
        this.justDoubleMoved = false;
        this.hasMoved = false;
    }

    @Override
    public String toString() {
        if (this.pieceType != null) {
            return this.teamColor.toString() + " " + this.pieceType.toString();
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return this.teamColor.equals(that.teamColor) && this.pieceType.equals(that.pieceType);
    }

    @Override
    public int hashCode() {
        int result = this.teamColor.hashCode();
        result = 31 * result + this.pieceType.hashCode();
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
        return this.teamColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.pieceType;
    }

    public boolean getJustDoubleMoved() {
        return this.justDoubleMoved;
    }

    public void setJustDoubleMoved(boolean setting) {
        this.justDoubleMoved = setting;
    }

    public boolean getHasMoved() {
        return this.hasMoved;
    }

    public void setHasMoved(boolean setting) {
        this.hasMoved = setting;
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

        if (this.pieceType == PieceType.KING || this.pieceType == PieceType.QUEEN || this.pieceType == PieceType.BISHOP || this.pieceType == PieceType.ROOK || this.pieceType == PieceType.KNIGHT) {
            calculateMovesForPieceType(board, myPosition, moves);
        }

        if (this.pieceType == PieceType.KING && !this.hasMoved) {
            addCastlingMoves(board, myPosition, moves);
        }

        if (this.pieceType == PieceType.PAWN) {
            addPawnMoves(board, myPosition, moves);
        }

        return moves;
    }

    private void calculateMovesForPieceType(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
        int[][] tuples = getMoveTuples(this.pieceType);
        ChessPosition i;

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
                } else {
                    moves.add(j);
                }

                if (this.pieceType == PieceType.KING || this.pieceType == PieceType.KNIGHT) {
                    break;
                }
            }
        }
    }

    private int[][] getMoveTuples(PieceType pieceType) {
        return switch (pieceType) {
            case BISHOP -> new int[][]{{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
            case QUEEN, KING -> new int[][]{{1, 1}, {1, -1}, {-1, 1}, {-1, -1}, {1, 0}, {-1, 0}, {0, 1}, {0, -1}};
            case ROOK -> new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
            case KNIGHT -> new int[][]{{1, 2}, {1, -2}, {-1, 2}, {-1, -2}, {2, 1}, {2, -1}, {-2, 1}, {-2, -1}};
            default -> throw new IllegalArgumentException("Unknown piece type: " + pieceType);
        };
    }

    private void addCastlingMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
        // Castle Left
        if (board.getPiece(new ChessPosition(board.getPosition(this).getRow(), board.getPosition(this).getColumn()-1)) == null) {
            if (board.getPiece(new ChessPosition(board.getPosition(this).getRow(), board.getPosition(this).getColumn()-2)) == null) {
                leftCastleHelper(board, moves);
            }
        }

        // Castle Right
        if (board.getPiece(new ChessPosition(board.getPosition(this).getRow(), board.getPosition(this).getColumn()+1)) == null) {
            if (board.getPiece(new ChessPosition(board.getPosition(this).getRow(), board.getPosition(this).getColumn()+2)) == null) {
                if (board.getPiece(new ChessPosition(board.getPosition(this).getRow(), board.getPosition(this).getColumn()+3)) != null) {
                    if (!board.getPiece(new ChessPosition(board.getPosition(this).getRow(), board.getPosition(this).getColumn()+3)).getHasMoved()) {
                        ChessMove castleMove = new ChessMove(board.getPosition(this), new ChessPosition(board.getPosition(this).getRow(), board.getPosition(this).getColumn()+2), null);
                        castleMove.setIsCastleMove(true);
                        moves.add(castleMove);
                    }
                }
            }
        }
    }

    private void leftCastleHelper(ChessBoard board, Collection<ChessMove> moves) {
        if (board.getPiece(new ChessPosition(board.getPosition(this).getRow(), board.getPosition(this).getColumn()-3)) == null) {
            if (board.getPiece(new ChessPosition(board.getPosition(this).getRow(), board.getPosition(this).getColumn()-4)) != null) {
                if (!board.getPiece(new ChessPosition(board.getPosition(this).getRow(), board.getPosition(this).getColumn()-4)).getHasMoved()) {
                    ChessMove castleMove = new ChessMove(board.getPosition(this), new ChessPosition(board.getPosition(this).getRow(), board.getPosition(this).getColumn()-2), null);
                    castleMove.setIsCastleMove(true);
                    moves.add(castleMove);
                }
            }
        }
    }

    private void addPawnMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
        int direction = (this.teamColor == ChessGame.TeamColor.BLACK) ? -1 : 1;

        // Move forward one space
        ChessPosition i = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn());
        if (board.getPiece(i) == null && i.getRow() != 8 && i.getRow() != 1) {
            moves.add(new ChessMove(myPosition, i, null));
            i = new ChessPosition(i.getRow() + direction, i.getColumn());
            if (board.getPiece(i) == null && ((i.getRow() == 4 && this.getTeamColor() == ChessGame.TeamColor.WHITE) || (i.getRow() == 5 && this.getTeamColor() == ChessGame.TeamColor.BLACK))) {
                moves.add(new ChessMove(myPosition, i, null));
            }
        } else if (board.getPiece(i) == null && (i.getRow() == 8 || i.getRow() == 1)) {
            addPawnPromotions(myPosition, i, moves);
        }

        addPawnAttacks(board, myPosition, direction, moves);
        addEnPassant(board, myPosition, direction, moves);
    }

    private void addPawnPromotions(ChessPosition startPos, ChessPosition endPos, Collection<ChessMove> moves) {
        moves.add(new ChessMove(startPos, endPos, PieceType.QUEEN));
        moves.add(new ChessMove(startPos, endPos, PieceType.ROOK));
        moves.add(new ChessMove(startPos, endPos, PieceType.BISHOP));
        moves.add(new ChessMove(startPos, endPos, PieceType.KNIGHT));
    }

    private void addPawnAttacks(ChessBoard board, ChessPosition myPosition, int direction, Collection<ChessMove> moves) {
        ChessPosition i = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn() - 1);
        pawnAttackHelper(board, myPosition, moves, i);

        i = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn() + 1);
        pawnAttackHelper(board, myPosition, moves, i);
    }

    private void pawnAttackHelper(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves, ChessPosition i) {
        if (board.getPiece(i) != null && board.getPiece(i).getTeamColor() != this.getTeamColor()) {
            if (i.getRow() == 1 || i.getRow() == 8) {
                addPawnPromotions(myPosition, i, moves);
            } else {
                moves.add(new ChessMove(myPosition, i, null));
            }
        }
    }

    private void addEnPassant(ChessBoard board, ChessPosition myPosition, int direction, Collection<ChessMove> moves) {
        ChessPosition i = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn() - 1);
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

} //