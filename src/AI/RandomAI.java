package AI;

import material.Board;
import material.Move;
import material.MoveException;
import material.Square;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class RandomAI extends AI {
	
	public void doMove(Board board) throws MoveException {
		HashSet<Square> squares = board.getSquares(board.getPerspective());
		ArrayList<Move> moves = new ArrayList<Move>();
		for (Square sq : squares) {
			moves.addAll(board.validMoves(sq));
		}
		Move m = randomMove(moves);
		board.makeMove(m);
	}
	
	private Move randomMove(ArrayList<Move> moves) {
		int size = moves.size();
		int item = new Random().nextInt(size);
		return moves.get(item);
	}
}
