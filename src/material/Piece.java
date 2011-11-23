package material;

import material.Board;
import material.Square;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Piece {
	private Square position;
	private String type;
	private String color;
	private Square move;
	private boolean hasMoved=false;
	private Board board;
	private Set<Square> validMoves;
	
	Piece(Square position, String type, String color) {
		this.position = position;
		this.type = type;
		this.color = color;
	}

	public List checkBoard(Square move, Board board) throws Exception {
		this.move = move;
		this.board = board;
		
		List<Boolean> moveChecks = new ArrayList<Boolean>(); 
		setValidMoves(position);
		moveChecks.add(isValidMove());
		boolean check = isCheck(oppColor());
		board.setCheck(check);
		moveChecks.add(check);
		moveChecks.add(isCheckmate(check));
		return moveChecks;
	}
	
	private boolean checkDiagMove(Square move) {
		int row, col;
		if (position.col < move.col && position.row < move.row) {
			row = position.row + 1;
			for (col = position.col + 1; col<move.col; col++) {
				if (squareOccupied(new Square(col,row))) return true;
				row += 1;
			}
		} else if (position.col < move.col && position.row > move.row) {
			row = position.row - 1;
			for (col = position.col + 1; col<move.col; col++) {
				if (squareOccupied(new Square(col,row))) return true;
				row -= 1;
			}
		} else if (position.col > move.col && position.row < move.row) {
			row = position.row + 1;
			for (col = position.col - 1; col>move.col; col--) {
				if (squareOccupied(new Square(col,row))) return true;
				row += 1;

			}
		} else if (position.col > move.col && position.row > move.row) {
			row = position.row - 1;
			for (col = position.col - 1; col > move.col; col--) {
				if (squareOccupied(new Square(col,row))) return true;
				row -= 1;
			}
		}
		return false;
	}
	
	private boolean squareOccupied(Square s) {
		if (board.getSquare(s) != null) {
			return true;
		}
		return false;
	}
	
	public String getColor() {
		return color;
	}
	
	public Square getPosition() {
		return position;
	}
	
	public String getType() {
		return type;
	}
	
	public Set<Square> getValidMoves(Board board, boolean attack) {
		this.board = board;
		BlockCheck bc = new BlockCheck(attack);
		Set validMoves = new HashSet<Square>();
		if (type.equals("rook")) {
			validMoves = validRookMoves(bc);
		} else if (type.equals("knight")) {
			validMoves = validKnightMoves(bc);
		} else if (type.equals("bishop")) {
			validMoves = validBishopMoves(bc);
		} else if (type.equals("queen")) {
			validMoves = validQueenMoves(bc);
		} else if (type.equals("king")) {
			validMoves = validKingMoves(bc);
		} else if (type.equals("pawn")) {
			validMoves = validPawnMoves(bc);
		}
		if (!attack) validMoves = reduceToCheckMoves(validMoves);
		
		return validMoves;
	}
	
	private boolean isCheck(String oppColor) {
		try {
			for (Square sq : board.getAttackedSquares(oppColor)) {
				Piece attackedPiece = board.getSquare(sq);
				if (attackedPiece != null) {
					if (attackedPiece.type.equals("king") && attackedPiece.color.equals(oppColor)) return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private boolean isCheckmate(boolean check) throws Exception {
		if (check) {
			Piece oppKing = board.getKing(oppColor());
			BlockCheck bc = new BlockCheck();
			if (oppKing.getValidMoves(board, false).isEmpty()) {
				for (Piece p : board.getPieces(oppColor())) {
					for (Square m : p.getValidMoves(board, false)) {
						board.movePiece(p, m);
						if (bc.blocked(board, position, oppKing.getPosition())) {
							board.movePieceBack(p, m);
							return false;
						}
						board.movePieceBack(p, m);
					}
				}
				return true;
			}
		}
		return false;
	}
	
	private boolean isHorizontalMove(Square move) {
		return position.row == move.row;
	}
	
	private boolean isVerticalMove(Square move) {
		return position.col == move.col;
	}
	
	private boolean isValidMove () {
		try {
			if (position != move && validMove(move)) {
				board.movePiece(this, move);
				setPosition(move);
				board.setAttackedSquares();
				hasMoved = true;
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private boolean onBoard(Square s) {
		if (s.row >= 1 && s.col >=1 && s.row <= 8 && s.col <= 8) {
			return true;
		}
		return false;
	}
	
	protected String oppColor() {
		if (color.equals("white")) return "black";
		return "white";
	}
	
	private void reduceToCheckMoves() {
		for (Square m : new HashSet<Square>(validMoves)) {
			if (violatesCheck(m)) {
				validMoves.remove(m);
			}
		}
	}
	
	private Set<Square> reduceToCheckMoves(Set<Square> validMoves) {
		Set<Square> checkMoves = new HashSet<Square>();
		for (Square m : validMoves) {
			if (!violatesCheck(m))	checkMoves.add(m);
		}
		return checkMoves;
	}
	
	public void setPosition(Square p) {
		position = p;
	}
	
	private void setValidMoves(Square position) throws Exception {
		BlockCheck bc = new BlockCheck();
		if (type.equals("rook")) {
			validMoves = validRookMoves(bc);
		} else if (type.equals("knight")) {
			validMoves = validKnightMoves(bc);
		} else if (type.equals("bishop")) {
			validMoves = validBishopMoves(bc);
		} else if (type.equals("queen")) {
			validMoves = validQueenMoves(bc);
		} else if (type.equals("king")) {
			validMoves = validKingMoves(bc);
		} else if (type.equals("pawn")) {
			validMoves = validPawnMoves(bc);
		} else {
			throw new Exception("invalid piece type!");
		}
		reduceToCheckMoves();
	}
	
	public String toString() {
		return color + " " + type + " " + position.toString();
	}
	
	private boolean validMove(Square move) throws Exception {
		if (validMoves.contains(move)) {
			return true;
		}
		return false;
	}
	
	private Set<Square> validRookMoves(BlockCheck bc) {
		Set<Square> validMoves = new HashSet<Square>();
		try {
			for (int i = 1; i<=8; i++) {
				Square horMove = new Square(i, position.row);
				Square vertMove = new Square(position.col, i);
				if (!horMove.equals(position) && !bc.blocked(position, horMove)) validMoves.add(horMove);
				if (!vertMove.equals(position) && !bc.blocked(position, vertMove)) validMoves.add(vertMove);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return validMoves;
	}
	
	private Set<Square> validKnightMoves(BlockCheck bc) {
		Set<Square> validMoves = new HashSet<Square>();
		try {
			for (int i = -2; i<=2; i++) {
				if (i==0) continue;
				int rowOffset = (Math.abs(i) == 1) ? 2 : 1;
				Square sq1 = new Square(position.col + i, position.row + rowOffset);
				Square sq2 = new Square(position.col + i, position.row - rowOffset);
				if (onBoard(sq1) && !bc.blocked(position, sq1)) validMoves.add(sq1);
				if (onBoard(sq2) && !bc.blocked(position, sq2)) validMoves.add(sq2);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return validMoves;
	}
	
	private Set<Square> validBishopMoves(BlockCheck bc) {
		Set<Square> validMoves = new HashSet<Square>();
		try {
			int bRow = position.row, bCol = position.col;
			while (bRow < 8 && bCol < 8) {
				bRow += 1;
				bCol += 1;
				Square sq = new Square(bCol, bRow);
				if (!bc.blocked(position, sq)) validMoves.add(sq);
			}
			bRow = position.row; bCol = position.col;
			while (bRow < 8 && bCol > 1) {
				bRow += 1;
				bCol -= 1;
				Square sq = new Square(bCol, bRow);
				if (!bc.blocked(position, sq)) validMoves.add(sq);
			}
			bRow = position.row; bCol = position.col;
			while (bRow > 1 && bCol < 8) {
				bRow -= 1;
				bCol += 1;
				Square sq = new Square(bCol, bRow);
				if (!bc.blocked(position, sq)) validMoves.add(sq);
			}
			bRow = position.row; bCol = position.col;
			while (bRow > 1 && bCol > 1) {
				bRow -= 1;
				bCol -= 1;
				Square sq = new Square(bCol, bRow);
				if (!bc.blocked(position, sq)) validMoves.add(sq);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return validMoves;
	}
	
	private Set<Square> validQueenMoves(BlockCheck bc) {
		Set<Square> validMoves = new HashSet<Square>();
		validMoves = validRookMoves(bc);
		validMoves.addAll(validBishopMoves(bc));
		return validMoves;
	}
	
	private Set<Square> validKingMoves(BlockCheck bc) {
		Set<Square> validMoves = new HashSet<Square>();
		try {
			for (int i=-1; i<=1; i++) {
				for (int j=-1; j<= 1; j++) {
					if (i==0 && j==0) continue;
					Square sq = new Square(position.col+i, position.row+j);
					if (onBoard(sq) && !bc.blocked(position, sq) && !board.getAttackedSquares(color).contains(sq)) validMoves.add(sq);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return validMoves;
	}
	
	private Set<Square> validPawnMoves(BlockCheck bc) {
		Set<Square> validMoves = new HashSet<Square>();
		Square moveUp, moveUp2, capture1, capture2;
		try {
			if (color.equals("white")) {
				moveUp = new Square(position.col, position.row+1);
				moveUp2 = new Square(position.col, position.row+2);
				capture1 = new Square(position.col-1, position.row+1);
				capture2 = new Square(position.col+1, position.row+1);
			} else {
				moveUp = new Square(position.col, position.row-1);
				moveUp2 = new Square(position.col, position.row-2);
				capture1 = new Square(position.col-1, position.row-1);
				capture2 = new Square(position.col+1, position.row-1);
			}
			if (onBoard(moveUp) && !squareOccupied(moveUp) && !bc.blocked(position, moveUp)) {
				validMoves.add(moveUp);
			}
			if (validPawnCapture(capture1)) validMoves.add(capture1);
			if (validPawnCapture(capture2)) validMoves.add(capture2);
			if (!hasMoved && !squareOccupied(moveUp2) && !bc.blocked(position, moveUp2)) {
				validMoves.add(moveUp2);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return validMoves;
	}
	
	private boolean validPawnCapture(Square sq) {
		if (squareOccupied(sq)) {
			if (board.getSquare(sq).color.equals(oppColor())) {
				return true;
			}
		}
		return false;
	}
	
	private boolean violatesCheck(Square move) {
		try {
			board.movePiece(this, move);
			if (isCheck(color)) {
				board.movePieceBack(this, move);
				return true;
			}
			board.movePieceBack(this, move);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public class BlockCheck {
		boolean attack = false;
		
		public BlockCheck() {}
		
		public BlockCheck(boolean attack) {
			this.attack = attack;
		}
		
		private boolean blocked(Square position, Square move) throws Exception {
			if (attack) {
				return blockedAttack(board, position, move);
			}
			return blockedMove(board, position, move);
		}
		
		boolean blocked(Board b, Square position, Square move) {
			if (attack) {
				return blockedAttack(b, position, move);
			}
			return blockedMove(b, position, move);
		}
		
		private boolean blockedMove(Board b, Square position, Square move) {
			Integer lowRow, lowCol, highRow, highCol;
			
			if (position.row < move.row) {
				lowRow = position.row;
				highRow = move.row;
			} else {
				lowRow = move.row;
				highRow = position.row;
			}
			
			if (position.col < move.col) {
				lowCol = position.col;
				highCol = move.col;
			} else {
				lowCol = move.col;
				highCol = position.col;
			}
			
			Piece oppPiece = null;
			boolean pieceFlag = true;
			if (squareOccupied(move)) {
				oppPiece = b.getSquare(move);
			} else {
				pieceFlag = false;
			}
			if (isHorizontalMove(move)) {
				for (int i = lowCol+1; i<highCol; i++) {
					if (squareOccupied(new Square(i, position.row))) return true;
				}
			} else if (isVerticalMove(move)) {
				for (int i = lowRow+1; i<highRow; i++) {
					if (squareOccupied(new Square(position.col, i))) return true;
				}
			} else if (type.equals("knight")) {
				if (pieceFlag) {
					if (oppPiece.color.equals(color)) return true;
				}
				return false;
			} else {
				if (checkDiagMove(move)) return true;
			}
			if (pieceFlag) {
				if (color.equals(oppPiece.color)) return true;
			}
			return false;
		}
		
		private boolean blockedAttack (Board b, Square position, Square move) {
			Integer lowRow, lowCol, highRow, highCol;
			
			if (position.row < move.row) {
				lowRow = position.row;
				highRow = move.row;
			} else {
				lowRow = move.row;
				highRow = position.row;
			}
			
			if (position.col < move.col) {
				lowCol = position.col;
				highCol = move.col;
			} else {
				lowCol = move.col;
				highCol = position.col;
			}
			
			if (isHorizontalMove(move)) {
				for (int i = lowCol+1; i<highCol; i++) {
					if (squareOccupied(new Square(i, position.row))) return true;
				}
			} else if (isVerticalMove(move)) {
				for (int i = lowRow+1; i<highRow; i++) {
					if (squareOccupied(new Square(position.col, i))) return true;
				}
			} else if (type.equals("knight")) {
				return false;
			} else {
				if (checkDiagMove(move)) return true;
			}
			return false;
		}
	}
}
