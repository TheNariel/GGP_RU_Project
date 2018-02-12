package galp.ggp.alphabeta;

import java.util.List;

import galp.ggp.statemachine.BitSetMachineState;

public class ABNode {
	List<ABNode> next;
	ABNode parent;
	boolean terminal = false;
	int b, a, value;
	BitSetMachineState state;

	public ABNode(ABNode parent, BitSetMachineState state) {
		super();
		this.parent = parent;
		this.state = state;
	}
}
