package material;

public class Rook extends Piece {

    public Rook(Color color) {
        super(color);
        setAbbr("R");
    }

    public boolean validate(Move move) {
        return move.isHorizontal() || move.isVertical();
    }
}
