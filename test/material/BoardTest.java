package material;

import static org.junit.Assert.*;

import java.util.HashSet;

import org.junit.*;

public class BoardTest {
	
	@Test
	public void testBoard() {
		Board board = new Board();
		assertNotNull(board.getPieces());
	}

	@Test
	public void testAdvancedPawn() {
		Board board = new Board();
		board.clear();
		
		Piece wPawn1 = new Pawn(Color.WHITE);
		Piece wPawn2 = new Pawn(Color.WHITE);
		Piece bPawn1 = new Pawn(Color.BLACK);
		Piece bPawn2 = new Pawn(Color.BLACK);
		Piece knight = new Knight(Color.WHITE);
		
		board.addPiece(1, 1, wPawn1);
		board.addPiece(6, 1, wPawn2);
		board.addPiece(6, 6, bPawn1);
		board.addPiece(1, 6, bPawn2);
		board.addPiece(5, 3, knight);
		
		Move wf = new Move(1, 1, 3, 1);
		Move wt = new Move(6, 1, 7, 1);
		Move bf = new Move(6, 6, 6, 5);
		Move bt = new Move(1, 6, 0, 6);
		Move kf = new Move(5, 3, 7, 3);
		
		
		board.movePiece(wf);
		assertFalse(board.advancedPawn(wf));
		
		board.movePiece(wt);
		assertTrue(board.advancedPawn(wt));
		
		board.movePiece(bf);
		assertFalse(board.advancedPawn(bf));
		
		board.movePiece(bt);
		assertTrue(board.advancedPawn(bt));
		
		board.movePiece(kf);
		assertFalse(board.advancedPawn(kf));
	}

	@Test
	public void testGetAttacking() {
		Board board = new Board();
		board.clear();
		board.addPiece(1, 3, new Pawn(Color.WHITE));
		
		HashSet<Move> attacking = board.getAttacking(Color.WHITE);
		Move m1 = new Move(1, 3, 2, 2);
		Move m2 = new Move(1, 3, 2, 4);

		assertTrue(attacking.contains(m1));
		assertTrue(attacking.contains(m2));
		assertEquals(attacking.size(), 2);
		
		attacking = board.getAttacking(Color.BLACK);
		assertTrue(attacking.isEmpty());
	}
	
	@Test
	public void testIsChecked() {
		Board board = new Board();
		assertFalse(board.isChecked(Color.WHITE));
		assertFalse(board.isChecked(Color.BLACK));
		
		Piece wKing = new King(Color.WHITE);
		Piece bKing = new King(Color.BLACK);
		Piece wQueen = new Queen(Color.WHITE);
		Piece bQueen = new Queen(Color.BLACK);
		
		board.clear();
		board.addPiece(0, 4, wKing);
		board.addPiece(1, 4, bQueen);
		board.addPiece(7, 5, bKing);
		board.addPiece(6, 5, wQueen);
		
		assertTrue(board.isChecked(Color.WHITE));
		assertTrue(board.isChecked(Color.BLACK));
		
	}

	@Test
	public void testIsCheckMate() {
		Board board = new Board();
		board.clear();
		
		Piece wKing = new King(Color.WHITE);
		Piece bKing = new King(Color.BLACK);
		Piece wQueen = new Queen(Color.WHITE);
		Piece wPawn = new Pawn(Color.WHITE);
		
		board.addPiece(0, 4, wKing);
		board.addPiece(7, 4, bKing);
		board.addPiece(6, 4, wQueen);
		board.addPiece(5, 3, wPawn);
		
		assertFalse(board.isCheckMate(Color.WHITE));
		assertTrue(board.isCheckMate(Color.BLACK));
	}

	@Test
	public void testIsValidCastle() throws MoveException {
		Board board = new Board();
		board.clear();
		
		board.addPiece(0, 4, new King(Color.WHITE));
		board.addPiece(7, 4, new King(Color.BLACK));
		board.addPiece(0, 0, new Rook(Color.WHITE));
		board.addPiece(7, 0, new Rook(Color.BLACK));
		board.addPiece(0, 7, new Rook(Color.WHITE));
		board.addPiece(7, 7, new Rook(Color.BLACK));
		
		Move wc1 = new Move(0, 4, 0, 2);
		Move wc2 = new Move(0, 4, 0, 6);
		Move bc1 = new Move(7, 4, 7, 2);
		Move bc2 = new Move(7, 4, 7, 6);
		
		Move[] cMoves = {wc1, wc2, bc1, bc2};
		for (Move move : cMoves) {
			assertTrue(board.isValidCastle(move));
		}
		
		Piece rook = new Rook(Color.BLACK);
		board.addPiece(2, 3, rook);
		assertFalse(board.isValidCastle(wc1));
		assertTrue(board.isValidCastle(wc2));
		
		board.removePiece(rook);
		board.addPiece(2, 3, rook);
		assertFalse(board.isValidCastle(wc1));
		
		board.removePiece(rook);
		board.addPiece(2, 2, rook);
		assertFalse(board.isValidCastle(wc1));
		board.removePiece(rook);
		
		board.makeMove(new Move(0, 0, 1, 0));
		board.makeMove(new Move(7, 4, 7, 3));
		board.makeMove(new Move(1, 0, 0, 0));
		board.makeMove(new Move(7, 3, 7, 4));
		board.addPiece(0, 6, new Knight(Color.WHITE));
		
		for (Move move : cMoves) {
			assertFalse(move.toString(), board.isValidCastle(move));
		}
	}
	
	@Test
	public void testIsValidEnpassant() throws MoveException, InterruptedException {
		Board board = new Board();
		board.clear();
		
		Piece wPawn = new Pawn(Color.WHITE);
		Piece bPawn = new Pawn(Color.BLACK);
		
		board.addPiece(1, 2, wPawn);
		board.addPiece(3, 1, bPawn);
		
		Move wMove = new Move(1, 2, 3, 2);
		Move bMove = new Move(3, 1, 2, 2);
		
		board.makeMove(wMove);
		assertTrue(board.isValidEnpassant(bMove));
	}
	
	@Test
	public void testValidMoves() {
		Board board = new Board();
		board.clear();
		
		Piece p = new Pawn(Color.WHITE);
		Square s = new Square(1, 0);
		board.addPiece(s, p);
		
		HashSet<Move> moves = board.validMoves(s);
		assertTrue(moves.contains(new Move(s, new Square(2, 0))));
		assertTrue(moves.contains(new Move(s, new Square(3, 0))));
		assertEquals(moves.size(), 2);
		
		moves = board.validMoves(new Square(2, 0));
		assertTrue(moves.isEmpty());
	}

	@Test
	public void testMakeMove() throws MoveException {
		Board board = new Board();
		board.clear();
		Color pers = board.getPerspective();
		Piece p = new Pawn(pers);
		board.addPiece(1, 0, p);
		Move move = new Move(1, 0, 2, 0);
		
		board.makeMove(move);
		
		assertTrue(p.getHasMoved());
		assertNotEquals(pers, board.getPerspective());
		assertEquals(board.getLastMove(), move);
		
	}

	@Test
	public void testMovePiece() {
		Board board = new Board();
		board.clear();
		
		Piece p = new Pawn(Color.WHITE);
		board.addPiece(1, 0, p);
		Move move = new Move(1, 0, 3, 0);
		board.movePiece(move);
		
		assertNull(board.getPiece(1, 0));
		assertEquals(board.getPiece(3, 0), p);
	}
}