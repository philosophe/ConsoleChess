package run;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;

import material.Board;
import material.Color;
import material.Move;
import material.MoveException;
import material.Piece;

public class Main {

    private static Board board = new Board();
    private static boolean checkmate = false;
	private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    private static void printBoard() {
        System.out.println(board.toString());
    }

    private static void printMoves(HashSet<Move> moves) {
        for (Move move : moves) {
            System.out.println(move.toAlgebraic());
        }
    }

    private static Piece queryPawnAdvancement(Color color) throws IOException {
    	try {
    		System.out.println("Choose replacement for pawn [Queen, Rook, Knight, Bishop]:");
    		String pstr = br.readLine();
    		if (pstr.equals("Pawn")) {
    			throw new IllegalArgumentException("Can't promote pawn to pawn");
    		}
    		return Piece.buildPiece(pstr, color);
    	} catch (IllegalArgumentException e) {
    		System.out.println(e.getMessage());
    		queryPawnAdvancement(color);
    	}
    	return null;
    }
    
    static void doMove(String movestr) throws MoveException, IOException {
    	MoveParser parser = new MoveParser(movestr);
        Move move = parser.toMove();
        Color mover = board.getPerspective();
        board.makeMove(move);
        Color opp = board.getPerspective();
        printBoard();
        if (board.advancedPawn(move)) {
        	Piece p = queryPawnAdvancement(mover);
        	board.removePiece(move.getDest());
        	board.addPiece(move.getDest(), p);
        	printBoard();
        }
        if (board.isCheckMate(opp)) {
        	System.out.println(String.format("Checkmate!! %s wins!", mover));
        	checkmate = true;
        } else if (board.isChecked(opp)) {
        	System.out.println("Check!");
        }
    }
    
    public static void main(String[] args) {
        printBoard();
		while (!checkmate) {
			try {
				System.out.print(String.format("\n%s's Turn: ", board.getPerspective()));
				String move = br.readLine();
				if (move.contains("exit") || move.contains("quit")) {
					System.out.println("Goodbye!");
					System.exit(0);
				} else if (move.contains("show")) {
					printBoard();
                } else if (move.contains("valid")) {
                    MoveParser parser = new MoveParser(move.replaceAll("valid", ""));
                    HashSet<Move> moves = board.validMoves(parser.getStartSquare());
                    printMoves(moves);
				} else if (move.contains("attacking")) {
                    HashSet<Move> moves = board.getAttacking(board.getPerspective());
                    printMoves(moves);
                } else if (move.contains("attacked")) {
                    Color color = (board.getPerspective() == Color.WHITE) ? Color.BLACK : Color.WHITE;
                    HashSet<Move> moves = board.getAttacking(color);
                    printMoves(moves);
                } else {
                    doMove(move);
				}
			} catch (MoveException e) {
				System.out.println(e.getMessage());
			} catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
			} catch (IOException e) {
				System.out.println(e.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
    }
} 
