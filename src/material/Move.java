package material;

public class Move {

    protected Square src;
    protected Square dest;

    public Move(Square src, Square dest) {
        this.src = src;
        this.dest = dest;
    };

    public Move(int sRow, int sCol, int dRow, int dCol) {
        src = new Square(sRow, sCol);
        dest = new Square(dRow, dCol);
    }

    public Square getSrc() {
        return src;
    }

    public Square getDest() {
        return dest;
    }

    public int getRowDiff() {
        return Math.abs(getSignedRowDiff());
    }

    public int getColDiff() {
        return Math.abs(getSignedColDiff());
    }
    
    public int getSignedRowDiff() {
        return src.getRow() - dest.getRow();
    }

    public int getSignedColDiff() {
        return src.getCol() - dest.getCol();
    }

    public boolean isForward() {
        return src.getRow() < dest.getRow();
    }

    public boolean isHorizontal() {
        return src.getRow() == dest.getRow();
    }

    public boolean isStationary() {
        return src.equals(dest);
    }

    public boolean isVertical() {
        return src.getCol() == dest.getCol();
    }

    public boolean isDiagonal() {
        return getRowDiff() == getColDiff();
    }

    public String toAlgebraic() {
        return String.format("%s%s -> %s%s", toAlpha(src.getCol()), src.getRow() + 1, toAlpha(dest.getCol()), dest.getRow() + 1);
    }

    private String toAlpha(int col) {
        switch (col) {
            case 0: return "A";
            case 1: return "B";
            case 2: return "C";
            case 3: return "D";
            case 4: return "E";
            case 5: return "F";
            case 6: return "G";
            case 7: return "H";
            default: return null;
        }
    }

    public String toString() {
        return String.format("(%s, %s) -> (%s, %s)", src.getRow(), src.getCol(), dest.getRow(), dest.getCol());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Move) {
            Move other = (Move) o;
            return src.equals(other.getSrc()) && dest.equals(other.getDest());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return src.getRow() + src.getCol() + dest.getRow() + dest.getCol();
    }
}
