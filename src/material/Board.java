package material;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Board implements Cloneable {
	private HashMap<Square, Piece> setup;
	private Map<String, Set<Square>> attackedSquares;
	private boolean check;
	private Piece possibleCapture = null;
	
	public Board() throws Exception {
		setup = new HashMap<Square, Piece>();
		initPieces();
		setAttackedSquares();
	}
	
	private void addPiece(int column, int row, String type) {
		Square square = new Square(column, row);
		String color = (row < 3) ? "white" : "black";
		setup.put(square, new Piece(square, type, color));
	}
	
	public Object clone() {
		try {
			Board cloned = (Board) super.clone();
			for (Square sq : cloned.setup.keySet()) {
				Piece p = cloned.getSquare(sq);
				if (p != null) {
					cloned.setSquare(sq, new Piece(sq, p.getType(), p.getColor()));
				}
			}
			return cloned;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Set<Piece> getPieces(String color) {
		Set<Piece> pieces = new HashSet<Piece>();
		for (Piece p : setup.values()) {
			if (p != null) {
				if (p.getColor().equals(color)) pieces.add(p);
			}
		}
		return pieces;
	}
	
	public Piece getKing(String color) {
		for (Piece p : setup.values()) {
			if (p != null) {
				if (p.getType().equals("king") && p.getColor().equals(color)) {
					return p;
				}
			}
		}
		return null;
	}
	
	public Piece getSquare(Square sq) {
		return setup.get(sq);
	}
	
	private void initPieces () {
		int[] initColumns = {1,2,3,4,5,6,7,8};
		int[] initRows = {1,2,7,8};
		
		for (int col : initColumns) {
			for (int row : initRows) {
				if (row==2 || row==7) {
					addPiece(col,row,"pawn");
				} else if (col==1 || col==8) {
					addPiece(col,row,"rook");
				} else if (col==2 || col==7) {
					addPiece(col, row, "knight");
				} else if (col==3 || col==6) {
					addPiece(col, row, "bishop");
				} else if (col==4) {
					addPiece(col, row, "queen");
				} else {
					addPiece(col, row, "king");
				}
			}
		}
	}
	
	public boolean isCheck(String oppColor) {
		try {
			for (Square sq : getAttackedSquares(oppColor)) {
				Piece attackedPiece = getSquare(sq);
				if (attackedPiece != null) {
					if (attackedPiece.getType().equals("king") && attackedPiece.getColor().equals(oppColor)) return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
//	public boolean isCheckmate(boolean check, String oppColor, Square position) throws Exception {
//		if (check) {
//			Piece oppKing = getKing(oppColor);
//			BlockCheck bc = oppKing.new BlockCheck();
//			if (oppKing.getValidMoves(this, false).isEmpty()) {
//				for (Piece p : getPieces(oppColor)) {
//					for (Square m : p.getValidMoves(this, false)) {
//						movePiece(p, m);
//						if (bc.blocked(this, position, oppKing.getPosition())) {
//							movePieceBack(p, m);
//							return false;
//						}
//						movePieceBack(p, m);
//					}
//				}
//				return true;
//			}
//		}
//		return false;
//	}
	
	public void movePiece(Piece p, Square m) {
		setup.put(p.getPosition(), null);
		Piece opp;
		possibleCapture =  ((opp = setup.get(m)) != null) ? opp : null;
		setup.put(m, p);
		try {
			setAttackedSquares();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void movePieceBack(Piece p, Square m) {
		setup.put(m, possibleCapture);
		setup.put(p.getPosition(), p);
		try {
			setAttackedSquares();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setAttackedSquares() throws Exception {
		attackedSquares = new HashMap<String, Set<Square>>();
		attackedSquares.put("white", new HashSet<Square>());
		attackedSquares.put("black", new HashSet<Square>());
		for (Square sq : new HashSet<Square>(setup.keySet())) {
			Piece piece = setup.get(sq);
			if (piece != null) {
				attackedSquares.get(piece.oppColor()).addAll(piece.getValidMoves(this, true));
			}
		}
	}
	
	public Set<Square> getAttackedSquares(String color) throws Exception {
		return attackedSquares.get(color);
	}
	
	public void setCheck(boolean check) { 
		this.check = check;
	}
	
	public void setSquare(Square sq, Piece p) {
		setup.put(sq, p);
	}
	
	private static String printLetter(String type) {
		if (type == "king") return "K";
		else if (type == "queen") return "Q";
		else if (type == "rook") return "R";
		else if (type == "knight") return "N";
		else if (type == "bishop") return "B";
		else return "P";
	}
	
	public String toString() {
		String alphaRow = "\t      A    B    C    D    E    F    G    H";
		String out = "\n" + alphaRow + "\n\n";
		for (int row = 8; row >= 1; row--) {
			out += "\t" + row + "  | ";
			for (int col = 1; col <= 8; col++) {
				Piece piece = getSquare(new Square(col, row));
				if (piece == null) {
					out += "-- | ";
				} else {
					out += piece.getColor().substring(0,1) + printLetter(piece.getType()) + " | ";
				}
			}
			out += " " + row + "\n\n";
		}
		out += alphaRow + "\n";
		return out;
	}
	
}
