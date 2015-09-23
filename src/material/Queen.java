package material;

public class Queen extends Piece {

    public Queen(Color color) {
        super(color);
        setAbbr("Q");
    }

    public boolean validate(Move move) {
        return move.isHorizontal() || 
               move.isVertical() ||
               move.isDiagonal();
    }
}
