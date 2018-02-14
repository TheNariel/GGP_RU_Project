package galp.ggp.gamer;

import org.ggp.base.util.statemachine.Move;

public class Transposition {
	int bestValue,  d;
	String type;
	Move bmove;
	public Transposition(int bestValue, int d, String string, Move bmove) {
		this.bestValue=bestValue;
		this.d=d;
		this.type = string;
		this.bmove= bmove;
	}

}
