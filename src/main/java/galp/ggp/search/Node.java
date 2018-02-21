package galp.ggp.search;

import java.util.Hashtable;

import galp.ggp.statemachine.BitSetMachineState;

public class Node {
	public int[][] Q, N;
	Hashtable<String, Node> next = new Hashtable<String, Node>();
	Node parent;
	public BitSetMachineState state;

	public Node(BitSetMachineState state, Node parent, Hashtable<String, Node> next, int[][] q, int[][] n) {
		super();
		this.state = state;
		this.parent = parent;
		this.next = next;
		Q = q;
		N = n;
	}

	public int getNodeN() {
		int ret = 0;
		for (int r = 1; r < N.length; r++) {
			for (int m = 1; m < N[r].length; m++) {
				ret += N[r][m];
			}
		}
		return ret;
	}
}
