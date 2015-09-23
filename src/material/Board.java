package material;

import java.util.HashMap;
import java.util.HashSet;

public class Board {
    
    private HashMap<Piece, Square> pieces;
    private Color perspective = Color.WHITE;
    private Move lastMove;

    public Board() {
        pieces = new HashMap<Piece, Square>();
        setupHomeRow(0, Color.WHITE);
        setupPawnRow(1, Color.WHITE);
        setupPawnRow(6, Color.BLACK);
        setupHomeRow(7, Color.BLACK);
    }

    public void addPiece(Square s, Piece p) {
        pieces.put(p, s);
    }

    protected void addPiece(int row, int column, Piece p) {
        pieces.put(p, new Square(row, column));
    }

    public boolean advancedPawn(Move move) {
    	Square dest = move.getDest();
    	Piece p = getPiece(dest);
    	if (p != null && p instanceof Pawn) {
    		int row = dest.getRow();
    		if (row == 0 || row == 7) {
    			return true;
    		}
    	}
    	return false;
    }
    
    protected void clear() {
    	pieces = new HashMap<Piece, Square>();
    }
    
    protected boolean isValidEnpassant(Move move) {
        if (lastMove != null) {
            Square lastDest = lastMove.getDest();
            Square curSrc = move.getSrc();
            Square curDest = move.getDest();
            Piece lastPiece = getPiece(lastDest);
            Piece p = getPiece(curSrc);
            if (lastPiece != null && p != null) {
                int attackRow = (perspective == Color.WHITE) ? lastDest.getRow() + 1 : lastDest.getRow() - 1;
                return lastPiece instanceof Pawn &&
                       p instanceof Pawn && // both have to be pawns
                       lastMove.getRowDiff() == 2 && // the opponent's pawn had to move out 2 squares
                       curSrc.getRow() == lastDest.getRow() &&  // in same row
                       Math.abs(curSrc.getCol() - lastDest.getCol()) == 1 && // in adjacent columns
                       curDest.getCol() == lastDest.getCol() &&
                       curDest.getRow() == attackRow; // have to be attacking the same row and column that the opponent pawn occupies
            }
        }
        return false;
    }

    public HashSet<Move> getAttacking(Color color) {
        HashSet<Move> attacked = new HashSet<Move>();
        for (Piece p : pieces.keySet()) {
            if (p.getColor() == color) {
                Square src = pieces.get(p);
                for (int row = 0; row < 8; row++) {
                    for (int col = 0; col < 8; col++) {
                        Square dest = new Square(row, col);
                        Move move = new Move(src, dest);
                        try {
                            validate(move, true, color); 
                            attacked.add(move);
                        } catch (MoveException ex) {}
                    }
                }
            }
        }
        return attacked;
    }

    public Move getLastMove() {
    	return lastMove;
    }
    
    public Color getPerspective() {
        return perspective;
    }

    protected Piece getPiece(Square square) {
        return getPiece(square.getRow(), square.getCol());
    }

    protected Piece getPiece(int row, int col) {
        for (Piece p : pieces.keySet()) {
            Square s = pieces.get(p);
            if (s.getRow() == row && s.getCol() == col) return p;
        }
        return null;
    }

    HashMap<Piece, Square> getPieces() {
    	return pieces;
    }
    
    public boolean isChecked(Color pers) {
        Color opp = (pers == Color.WHITE) ? Color.BLACK : Color.WHITE;
        HashSet<Move> attacked = getAttacking(opp);
        for (Move move : attacked) {
            Piece p = getPiece(move.getDest());
            if (p instanceof King && p.getColor() == pers) return true;
        }
        return false;
    }
    
    public boolean isCheckMate(Color pers) {
    	for (int row=0; row < 8; row++) {
    		for (int col=0; col < 8; col++) {
    			Piece p = getPiece(row, col);
    			if (p != null && p.getColor() == pers) {
    				HashSet<Move> moves = validMoves(pieces.get(p));
    				if (!moves.isEmpty())
    					return false;
    			}
    		}
    	}
    	return true;
    }

    private Piece validate(Move move, Boolean isCapturing, Color pers) throws MoveException {
        Square src = move.getSrc();
        Square dest = move.getDest();
        Piece p = getPiece(src);
        if (p == null) throw new MoveException("No piece on selected square");
        if (move.isStationary()) throw new MoveException("Can't move to the same square");

        Piece dp = getPiece(dest);
        boolean sameOcc = dp != null && dp.getColor() == pers;
        if (isValidEnpassant(move) && !sameOcc) return p;

        if (isCapturing == null) isCapturing = dp != null;
        if (!p.validate(move, isCapturing)) throw new MoveException("Invalid move");
        if (sameOcc) throw new MoveException("Square occupied by own piece");


        if (!(p instanceof Knight)) {
            while (!src.equals(dest)) {
                src = moveOne(src, dest);
                if (getPiece(src) != null && !src.equals(dest)) {
                    throw new MoveException("Path is blocked");
                }
            }
        }

        return p;
    }

    private void validateCheck(Move move, Color pers) throws MoveException {
        Square src = move.getSrc();
        Square dest = move.getDest();
        Piece p = getPiece(src);
        Piece dp = getPiece(dest);

        Rollback roll = new Rollback(p, dp, move.getSrc(), pieces.get(dp));
        movePiece(move);
        if (isChecked(pers)) {
            rollback(roll);
            throw new MoveException("Move introduces or fails to remove check");
        }
        rollback(roll);
    }

    private void rollback(Rollback roll) {
        removePiece(roll.getSrc()); 
        if (roll.getCapture() != null) {  
            addPiece(roll.getCaptureDest(), roll.getCapture());
        }
        addPiece(roll.getSrcDest(), roll.getSrc());
    }

    private Piece validate(Move move, Color pers) throws MoveException {
        return validate(move, null, pers);
    }

    private Piece validate(Move move) throws MoveException {
        return validate(move, null, perspective);
    }

    private Square moveOne(Square src, Square dest) {
        int newRow = src.getRow();
        int newCol = src.getCol();
        if (newRow != dest.getRow()) {
            newRow = (newRow < dest.getRow()) ? newRow + 1 : newRow - 1;
        }
        if (newCol != dest.getCol()) {
            newCol = (newCol < dest.getCol()) ? newCol + 1 : newCol - 1;
        }
        return new Square(newRow, newCol);
    }

    public HashSet<Move> validMoves(Square s) {
        HashSet<Move> moves = new HashSet<Move>();
        Piece p = getPiece(s);
        if (p != null) {
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    Move move = new Move(s, new Square(row, col));
                    try {
                        validate(move, p.getColor()); 
                        validateCheck(move, p.getColor());
                        moves.add(move);
                    } catch (MoveException e) {}
                }
            }
        }
        return moves;
    }

    public void makeMove(Move move) throws MoveException {
        Piece p = validate(move);
        if (p.getColor() != perspective) throw new MoveException("Tried to move the opponents piece");
        validateCheck(move, perspective);
        movePiece(move);
        perspective = (perspective == Color.WHITE) ? Color.BLACK : Color.WHITE;
        p.setHasMoved(true);
        lastMove = move;
    }

    protected void movePiece(Move move) {
        Piece p = getPiece(move.getSrc());
        if (isValidEnpassant(move)) removePiece(lastMove.getDest());
        removePiece(p);
        removePiece(move.getDest());
        addPiece(move.getDest(), p);
    }

    private void removePiece(int row, int col) {
        removePiece(getPiece(row, col));
    }

    public void removePiece(Square s) {
        removePiece(s.getRow(), s.getCol());
    }

    private void removePiece(Piece p) {
        pieces.remove(p);
    }

    protected void setPieces(HashMap<Piece, Square> pieces) {
    	this.pieces = pieces;
    }
    
    private void setupHomeRow(int row, Color color) {
        addPiece(row, 0, new Rook(color));
        addPiece(row, 1, new Knight(color));
        addPiece(row, 2, new Bishop(color));
        addPiece(row, 3, new Queen(color));
        addPiece(row, 4, new King(color));
        addPiece(row, 5, new Bishop(color));
        addPiece(row, 6, new Knight(color));
        addPiece(row, 7, new Rook(color));
    }

    private void setupPawnRow(int row, Color color) {
        for (int column = 0; column < 8; column++) {
            addPiece(row, column, new Pawn(color));
        }
    }

    public String toString() {
        String alphaRow;
        if (perspective == Color.WHITE) {
            alphaRow = "\t      A    B    C    D    E    F    G    H";
        } else {
            alphaRow = "\t      H    G    F    E    D    C    B    A";
        }
		String out = "\n" + alphaRow + "\n\n";
		for (int row = 7; row >= 0; row--) {
            int rowlabel = (perspective == Color.WHITE) ? row + 1 : 8 - row;
			out += "\t" + rowlabel + "  | ";
			for (int col = 0; col < 8; col++) {
				Piece piece = (perspective == Color.WHITE) ? getPiece(row, col) : getPiece(7-row, 7-col);
				if (piece == null) {
					out += "-- | ";
				} else {
					out += piece.toString() + " | ";
				}
			}
			out += " " + rowlabel + "\n\n";
		}
		out += alphaRow + "\n";
		return out;
    }
}
