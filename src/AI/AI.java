package AI;

import material.Board;
import material.MoveException;

public abstract class AI {
	
	
	
	public abstract void doMove(Board board) throws MoveException;
	
	public static AI load(String ai) {
		if (ai == null) return null;
		
		ai = ai.toLowerCase();
		switch (ai) {
			case "random": return new RandomAI();
			default: return null;
		}
	}
}
