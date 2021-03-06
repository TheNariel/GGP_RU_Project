package galp.ggp.search;

import java.util.Hashtable;
import java.util.List;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;

public class Node {
	public double[][] Q;
	public int[][] N;
	public List<List<Move>> legalActions;
	public Hashtable<String, Node> exploredChildren = new Hashtable<String, Node>();
	public Node parent;
	public MachineState state;
	public List<Integer> moveFromParent;
	public boolean terminal = false;
	public List<Integer> values;

	public Node(MachineState state, Node parent, List<List<Move>> legalActions, Hashtable<String, Node> next,
			double[][] q, int[][] n, List<Integer> moveFromParent) {
		super();
		this.state = state;
		this.parent = parent;
		this.exploredChildren = next;
		Q = q;
		N = n;
		this.legalActions = legalActions;
		this.moveFromParent = moveFromParent;
	}

	public int getNodeN() {
		int ret = 0;

		for (int m = 0; m < N[0].length; m++) {
			ret += N[0][m];
		}

		return ret;
	}
}
