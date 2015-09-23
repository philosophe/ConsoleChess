package material;

public class Bishop extends Piece {

    public Bishop(Color color) {
        super(color);
        setAbbr("B");
    }

    public boolean validate(Move move) {
        return move.isDiagonal();
    }
}
