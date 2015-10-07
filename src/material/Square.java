package material;

public class Square {

    private int row;
    private int col;
   
    public Square(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Square) {
            Square other = (Square) o;
            return (row == other.getRow()) && (col == other.getCol());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return row + col;
    }
}
