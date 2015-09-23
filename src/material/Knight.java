package material;

public class Knight extends Piece {

    public Knight(Color color) {
        super(color);
        setAbbr("N");
    }

    public boolean validate(Move move) {
        int rowDiff = move.getRowDiff();
        int colDiff = move.getColDiff();
        return rowDiff > 0 && colDiff > 0 && rowDiff + colDiff == 3;
    }
}
