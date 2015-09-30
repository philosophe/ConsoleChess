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
    
    public HashSet<Move> getAttacked(Color color) {
    	Color opp = (color.equals(Color.WHITE)) ? Color.BLACK : Color.WHITE;
    	return getAttacking(opp);
    }
    
    public HashSet<Move> getAttacking(Color color) {
    	HashSet<Move> att = new HashSet<Move>();
        for (Piece p : pieces.keySet()) {
            if (p.getColor() == color) {
                Square src = pieces.get(p);
                for (int row = 0; row < 8; row++) {
                    for (int col = 0; col < 8; col++) {
                        Square dest = new Square(row, col);
                        Move move = new Move(src, dest);
                        if (isValidAttack(move, color)) {
                        	att.add(move);
                        }
                    }
                }
            }
        }
        return att;
    }
    
    protected void clear() {
    	pieces = new HashMap<Piece, Square>();
    }
    
    private Square getCastlingCorner(Move move) {
    	Square src = move.getSrc();
		int direction = move.getSignedColDiff();
		if (direction == -2) {
			return new Square(src.getRow(), 7);
		} else if (direction == 2) {
			return new Square(src.getRow(), 0);
		}
		return null;
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
    
    private boolean introducesCheck(Move move, Color pers) {
    	Piece p = getPiece(move.getSrc());
    	Piece dp = (isValidEnpassant(move)) ? getPiece(lastMove.getDest()) : getPiece(move.getDest());

        Rollback roll = new Rollback(p, dp, move.getSrc(), pieces.get(dp));
        movePiece(move);
        boolean intCheck = isChecked(pers);
        rollback(roll);
        
        return intCheck;
    }
    
    public boolean isChecked(Color pers) {
        HashSet<Move> attacked = getAttacked(pers);
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

    private boolean isBlocked(Square src, Square dest) {
    	while (!src.equals(dest)) {
            src = moveOne(src, dest);
            if (getPiece(src) != null && !src.equals(dest)) {
                return true;
            }
        }
    	return false;
    }
    
    private boolean isValidAttack(Move move, Color pers) {
    	return wasMoved(move) && (isValidPieceMove(move, true) || isValidEnpassant(move));
    }
    
    protected boolean isValidCastle(Move move) {
    	Square src = move.getSrc();
    	Piece p = getPiece(src);
    	if (p instanceof King && !p.getHasMoved() && move.isHorizontal()) {
    		Square cSqr = getCastlingCorner(move);
    		if (cSqr != null) {
	    		Piece corner = getPiece(cSqr);
	    		if (corner instanceof Rook && !corner.getHasMoved() && !isBlocked(src, cSqr)) {
	    			Square k1 = null;
	    			Square k2 = null;
	    			if (cSqr.getCol() == 0) {
	    				k1 = new Square(src.getRow(), 3);
	    				k2 = new Square(src.getRow(), 2);
	    			} else {
	    				k1 = new Square(src.getRow(), 5);
	    				k2 = new Square(src.getRow(), 6);
	    			}
	    			HashSet<Move> attacked = getAttacked(p.getColor());
	    			for (Move attack : attacked) {
	    				Square attDest = attack.getDest();
	    				if (attDest.equals(k1) || attDest.equals(k2) || attDest.equals(src)) {
	    					return false;
	    				}
	    			}
	    			return true;
	    		}
    		}
    	}
    	return false;
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
    
    private boolean isValidMove(Move move, Color color) {
		return isValidMove(move, null, color);
	}
    
    private boolean isValidMove(Move move, Boolean isCapturing, Color pers) {
    	boolean validMove = isValidPieceMove(move, isCapturing) || isValidEnpassant(move) || isValidCastle(move);
    	return wasMoved(move) && validMove && !introducesCheck(move, pers);
    }
    
    private boolean isValidPieceMove(Move move, Boolean isCapturing) {
    	Piece p = getPiece(move.getSrc());
    	Piece dp = getPiece(move.getDest());
    	if (isCapturing == null) isCapturing = dp != null;
        if (!p.validate(move, isCapturing)) return false;

        if (!(p instanceof Knight)) {
        	if (isBlocked(move.getSrc(), move.getDest()))
        		return false;
        }
        return !sameOcc(move, p.getColor());
    }
    
    public void makeMove(Move move) throws MoveException {
    	if (!isValidMove(move, perspective))
    		throw new MoveException("Invalid Move");
    	Piece p = getPiece(move.getSrc());
        if (p.getColor() != perspective) throw new MoveException("Tried to move the opponents piece");
        //validateCheck(move, perspective);
        movePiece(move);
        perspective = (perspective == Color.WHITE) ? Color.BLACK : Color.WHITE;
        p.setHasMoved(true);
        lastMove = move;
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

    protected void movePiece(Move move) {
    	Piece p = getPiece(move.getSrc());
    	
        if (isValidEnpassant(move)) removePiece(lastMove.getDest());
        if (isValidCastle(move)) {
        	Square corner = getCastlingCorner(move);
        	Piece rook = getPiece(corner);
        	removePiece(rook);
        	if (corner.getCol() == 0) {
        		addPiece(corner.getRow(), 3, rook);
        	} else {
        		addPiece(corner.getRow(), 5, rook);
        	}
    	}
        
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

    public void removePiece(Piece p) {
        pieces.remove(p);
    }

    private void rollback(Rollback roll) {
        removePiece(roll.getSrc()); 
        if (roll.getCapture() != null) {  
            addPiece(roll.getCaptureDest(), roll.getCapture());
        }
        addPiece(roll.getSrcDest(), roll.getSrc());
    }
    
    private boolean sameOcc(Move move, Color pers) {
        Piece dp = getPiece(move.getDest());
        return dp != null && dp.getColor() == pers;
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
    
    public HashSet<Move> validMoves(Square s) {
        HashSet<Move> moves = new HashSet<Move>();
        Piece p = getPiece(s);
        if (p != null) {
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    Move move = new Move(s, new Square(row, col));
                    if (isValidMove(move, p.getColor())) {
                    	moves.add(move);
                    }
                }
            }
        }
        return moves;
    }

	private boolean wasMoved(Move move) {
        Piece p = getPiece(move.getSrc());
        return p != null && !move.isStationary();
    }
}
