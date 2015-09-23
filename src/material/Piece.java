package material;

public abstract class Piece {

    private Color color;
    private String abbr;
    private boolean hasMoved = false;

    public Piece(Color color) {
        this.color = color;
    }
    
    public static Piece buildPiece(String pstr, Color color) {
    	switch (pstr) {
    		case "Queen": return new Queen(color);
    		case "Rook": return new Rook(color);
    		case "Knight": return new Knight(color);
    		case "Bishop": return new Bishop(color);
    		case "Pawn": return new Pawn(color);
    		default: throw new IllegalArgumentException(String.format("Unknown piece string: %s", pstr));
    	}
    }

    public String getAbbr() {
        return abbr;
    }

    public Color getColor() {
        return color;
    }

    public boolean getHasMoved() {
        return hasMoved;
    }

    public void setAbbr(String abbr) {
        this.abbr = abbr;
    }

    public void setHasMoved(boolean moved) {
        hasMoved = moved;
    }

    public String toString() {
        String cl = color.toString().substring(0,1).toLowerCase();
        return cl + abbr;
    }

    public abstract boolean validate(Move move);

    public boolean validate(Move move, boolean isCapturing) {
        return validate(move);
    }
}
