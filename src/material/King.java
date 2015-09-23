package material;

public class King extends Piece {

    public King(Color color) {
        super(color);
        setAbbr("K");
    }

    public boolean validate(Move move) {
        return move.getRowDiff() < 2 && move.getColDiff() < 2;
    }
}
