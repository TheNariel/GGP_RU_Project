package galp.ggp.gamer;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

import org.ggp.base.apps.player.detail.DetailPanel;
import org.ggp.base.apps.player.detail.EmptyDetailPanel;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

import galp.ggp.search.Node;
import galp.ggp.statemachine.BitSetMachineState;
import galp.ggp.statemachine.TimeOutException;

public class MCGamer extends TrialSampleGamer {
	int nStates;

	@Override
	public Move stateMachineSelectMove(long timeout)
			throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {
		Node root = initNextNode((BitSetMachineState) getStateMachine().getInitialState(), null, null);
		nStates =0;
		mcSearch(root, timeout);

		Move bestMove = null;
		for (int r = 0; r < getStateMachine().getRoles().size(); r++) {
			if (getStateMachine().getRoles().get(r).equals(getRole())) {
				int i=0;
				int max = root.N[r][0];
				for (int m = 1; m < root.N[r].length; m++) {
					if (root.N[r][m] > max) {
						max=root.N[r][m];
						i=m;
					}
				}

				bestMove = root.legalActions.get(r).get(i);

			}
		}
		System.out.println(bestMove + " n of simulations: "+nStates);
		return bestMove;
	}

	@Override
	public void stateMachineMetaGame(long timeout)
			throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {

		System.out.println("::::META GAME START::::");
		Node root = initNextNode((BitSetMachineState) getStateMachine().getInitialState(), null, null);
		nStates =0;
		mcSearch(root, timeout);

		Move bestMove = null;
		for (int r = 0; r < getStateMachine().getRoles().size(); r++) {
			if (getStateMachine().getRoles().get(r).equals(getRole())) {
				int i=0;
				int max = root.N[r][0];
				for (int m = 1; m < root.N[r].length; m++) {
					if (root.N[r][m] > max) {
						max=root.N[r][m];
						i=m;
					}
				}

				bestMove = root.legalActions.get(r).get(i);

			}
		}
		System.out.println(bestMove + " n of simulations: "+nStates);
		System.out.println("::::META GAME END::::");
	}

	@Override
	public DetailPanel getDetailPanel() {
		return new EmptyDetailPanel();
	}

	private Node initNextNode(BitSetMachineState state, Node parent, List<Integer> moveFromParent)
			throws MoveDefinitionException {

		List<List<Move>> legalActions = new ArrayList<List<Move>>();
		for (Role role : getStateMachine().getRoles()) {
			legalActions.add(getStateMachine().getLegalMoves(state, role));
		}
		//System.out.println(legalActions);
		double[][] Q = new double[legalActions.size()][];
		int[][] N = new int[legalActions.size()][];
		int i = 0;
		for (List<Move> m : legalActions) {
			Q[i] = new double[m.size()];
			N[i] = new int[m.size()];
			i++;
		}
		Hashtable<String, Node> next = new Hashtable<String, Node>();

		return new Node(state, parent, legalActions, next, Q, N, moveFromParent);
	}

	public void mcSearch(Node node, long timeout)
			throws MoveDefinitionException, TransitionDefinitionException, GoalDefinitionException {
		List<Integer> indexes;
		List<Move> moves;
		String contents;

		while (System.currentTimeMillis() + 100 <= timeout) {
			// selection
			while (true) {
				moves = new ArrayList<Move>();
				indexes = selection(node);
				contents = "[";
				for (int i = 0; i < indexes.size(); i++) {
					Move m = node.legalActions.get(i).get(indexes.get(i));
					contents += m.toString() + ", ";
					moves.add(m);
				}
				contents = contents.substring(0, contents.length() - 2);
				contents += "]";
				if (node.next.contains(contents)) {
					node = node.next.get(contents);
				} else {
					break;
				}
			}

			// expand
			BitSetMachineState nextstate = (BitSetMachineState) getStateMachine().getNextState(node.state, moves);
			Node next = initNextNode((BitSetMachineState) nextstate, node, indexes);
			node.next.put(contents, next);

			// playout
			List<Integer> value;
			try {
				value = runSimulation(next.state, timeout);
			} catch (TimeOutException e) {
				System.out.println("no more time, Get out, out, out .... ");
				break;
			}

			// backprop
			backProp(next, value);

		}

	}

	public List<Integer> selection(Node node) {
		int C = 50;
		Random rand = new Random();
		List<List<Double>> A = new ArrayList<List<Double>>();

		List<Integer> indexes = new ArrayList<Integer>();

		for (int r = 0; r < node.N.length; r++) {
			List<Double> mo = new ArrayList<Double>();
			for (int m = 0; m < node.N[r].length; m++) {
				if (node.N[r][m] == 0) {
					mo.add((double) Integer.MAX_VALUE);
				} else {
					mo.add(node.Q[r][m] + C * Math.sqrt(Math.log10(node.getNodeN()) / node.N[r][m]));
				}
			}
			A.add(mo);
		}

		for (int r = 0; r < A.size(); r++) {
			double max = 0;
			List<Integer> mo = new ArrayList<Integer>();
			for (int m = 0; m < A.get(r).size(); m++) {
				if (A.get(r).get(m) > max) {
					mo.clear();
					mo.add(m);
					max = A.get(r).get(m);
				} else {
					if (A.get(r).get(m) == max) {
						mo.add(m);
					}
				}
			}
			indexes.add(mo.get(rand.nextInt(mo.size())));
		}

		return indexes;
	}

	public void backProp(Node node, List<Integer> value) {
		while (node.parent != null) {
			for (int i = 0; i < node.moveFromParent.size(); i++) {
				node.parent.Q[i][node.moveFromParent.get(i)] = node.Q[i][node.moveFromParent.get(i)]
						+ ((value.get(i) - node.Q[i][node.moveFromParent.get(i)]) / (node.getNodeN() + 1));
				node.parent.N[i][node.moveFromParent.get(i)] += 1;
			}
		}
	}

	public List<Integer> runSimulation(BitSetMachineState state, long timeout)
			throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException, TimeOutException {
		nStates++;
		while (!getStateMachine().isTerminal(state)) {
			if (System.currentTimeMillis() + 100 <= timeout)
				throw new TimeOutException();
			state = (BitSetMachineState) getStateMachine().getNextState(state,
					getStateMachine().getRandomJointMove(state));
		}

		List<Integer> ret = new ArrayList<Integer>();
		for (Role r : getStateMachine().getRoles()) {
			ret.add(getStateMachine().getGoal(state, r));
		}
		return ret;

	}

}