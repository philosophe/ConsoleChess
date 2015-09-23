package material;

public class Pawn extends Piece {

    public Pawn(Color color) {
        super(color);
        setAbbr("P");
    }

    public boolean validate(Move move) {
        return validate(move, false);
    }

    public boolean validate(Move move, boolean isCapturing) {
        boolean direction = (getColor() == Color.WHITE) ? move.isForward() : !move.isForward();
        if (!direction) return false;
        if (isCapturing) {
            return move.isDiagonal() && move.getRowDiff() == 1;
        } else {
            if (!move.isVertical()) return false;
            if (getHasMoved()) return move.getRowDiff() == 1;
            return move.getRowDiff() < 3;
        }
    }
}
