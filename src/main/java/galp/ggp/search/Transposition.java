package galp.ggp.search;

import org.ggp.base.util.statemachine.Move;

public class Transposition {
	public int bestValue;
	public int d;
	public String type;
	public Move bmove;
	public Transposition(int bestValue, int d, String string, Move bmove) {
		this.bestValue=bestValue;
		this.d=d;
		this.type = string;
		this.bmove= bmove;
	}

}
