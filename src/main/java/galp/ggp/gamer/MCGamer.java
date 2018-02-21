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

public class MCGamer extends TrialSampleGamer {
	int nStates;

	@Override
	public Move stateMachineSelectMove(long timeout)
			throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {
		/*
		 * nStates = 0; Move bestMove = null; System.out.println("starting the search");
		 * Long startTime = System.currentTimeMillis(); // bestMove =
		 * mcSearch(getRole(), (BitSetMachineState) getCurrentState(), // timeout); Long
		 * endTime = System.currentTimeMillis();
		 *
		 * System.out.println("Search done in: " + (endTime - startTime) +
		 * " ms(time left " + (timeout - endTime) + " ms) with best move: " +
		 * bestMove.toString() + " number of simulations: " + nStates); return bestMove;
		 */
		return null;
	}

	@Override
	public void stateMachineMetaGame(long timeout)
			throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {

		System.out.println("::::META GAME START::::");
		Node root = initNextNode((BitSetMachineState) getStateMachine().getInitialState(), null);

		System.out.println("::::META GAME END::::");
	}

	@Override
	public DetailPanel getDetailPanel() {
		return new EmptyDetailPanel();
	}

	private Node initNextNode(BitSetMachineState state, Node parent) throws MoveDefinitionException {

		List<List<Move>> legalActions = new ArrayList<List<Move>>();
		for (Role role : getStateMachine().getRoles()) {
			legalActions.add(getStateMachine().getLegalMoves(state, role));
		}
		int[][] Q = new int[legalActions.size()][];
		int[][] N = new int[legalActions.size()][];
		int i = 0;
		for (List<Move> m : legalActions) {
			Q[i] = new int[m.size()];
			N[i] = new int[m.size()];
			i++;
		}
		Hashtable<String, Node> next = new Hashtable<String, Node>();

		return new Node(state, parent, next, Q, N);
	}

	public Move mcSearchss(Role r, Node root, long timeout)
			throws MoveDefinitionException, TransitionDefinitionException, GoalDefinitionException {
		List<Move> legalMoves;
		BitSetMachineState nextState;
		int score, index;
		legalMoves = getStateMachine().getLegalMoves(root.state, r);

		int[] Q = new int[legalMoves.size()];
		int[] N = new int[legalMoves.size()];

		while (System.currentTimeMillis() + 100 <= timeout) {
			nStates++;
			List<Move> currMove = getStateMachine().getRandomJointMove(root.state);
			Move action = currMove.get(getStateMachine().getRoles().indexOf(r));

			nextState = (BitSetMachineState) getStateMachine().getNextState(root.state, currMove);
			score = runSimulation(r, nextState);

			index = legalMoves.indexOf(action);
			Q[index] = (Q[index] * N[index] + score) / (N[index] + 1);
			N[index] += 1;
		}

		int maxI = 0;
		for (int i = 1; i < Q.length; i++) {
			if (Q[i] > Q[maxI])
				maxI = i;
		}

		System.out.println("no more time, Get out, out, out .... ");
		return legalMoves.get(maxI);
	}

	public Move mcSearch(Role r, Node root, long timeout)
			throws MoveDefinitionException, TransitionDefinitionException, GoalDefinitionException {

		while (System.currentTimeMillis() + 100 <= timeout) {

		}

		System.out.println("no more time, Get out, out, out .... ");
		return null;
	}

	public List<Integer> selection(Node root) {
		int C =50;
		Random rand = new Random();
		List<List<Double>> pausible = new ArrayList<List<Double>>();

		for (int r = 1; r < root.N.length; r++) {
			List<Double> mo = new ArrayList<Double>();
			for (int m = 1; m < root.N[r].length; m++) {
				if (root.N[r][m] == 0) {
					mo.add((double) Integer.MAX_VALUE);
				}else {
					mo.add(root.Q[r][m]+C*Math.sqrt(Math.log10(root.getNodeN())/root.N[r][m]));
				}
			}


		}

		return null;
	}

	public int runSimulation(Role r, BitSetMachineState state)
			throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {

		while (!getStateMachine().isTerminal(state)) {
			state = (BitSetMachineState) getStateMachine().getNextState(state,
					getStateMachine().getRandomJointMove(state));
		}

		return getStateMachine().getGoal(state, r);

	}

}