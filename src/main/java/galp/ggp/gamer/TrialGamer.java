package galp.ggp.gamer;

import java.util.BitSet;
import java.util.Hashtable;
import java.util.List;

import org.ggp.base.apps.player.detail.DetailPanel;
import org.ggp.base.apps.player.detail.EmptyDetailPanel;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

import galp.ggp.statemachine.BitSetMachineState;
import galp.ggp.statemachine.TimeOutException;

public class TrialGamer extends TrialSampleGamer {
	private boolean bottomed;
	int nStates,transFound;

	Hashtable<BitSet, Transposition> trans_table ;
	@Override
	public Move stateMachineSelectMove(long timeout)
			throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {
		/*
		 * Random rng = new Random(); StateMachine theMachine = getStateMachine();
		 * List<Move> moves = theMachine.getLegalMoves(getCurrentState(), getRole());
		 *
		 * Move bestMove = moves.get(rng.nextInt(moves.size()));
		 */
		nStates = 0;
		Move bestMove = null;
		Long startTime = System.currentTimeMillis();

		System.out.println("starting the search at depth 1");
		try {

			for (int d = 1; d < 100; d++) {
				nStates = 0;
				transFound = 0;
				bottomed = false;
				System.out.println("search at depth " + d);

				Long startLoopTime = System.currentTimeMillis();
				bestMove = minMaxSearch((BitSetMachineState) getCurrentState(), d, timeout);
				// bestMove = minMaxSearchNoAB((BitSetMachineState) getCurrentState(), d,
				// timeout);
				Long endLoopTime = System.currentTimeMillis();
				System.out.println(
						"Depth searched done in: " + (endLoopTime - startLoopTime) + " ms and " + nStates + " nodes and "+transFound+" transpositions found "+trans_table.size());
				if (!bottomed)
					break;
			}

		} catch (TimeOutException e) {
			System.out.println("no more time, Get out, out, out .... ");
		}
		Long endTime = System.currentTimeMillis();
		System.out.println("Search done in: " + (endTime - startTime) + " ms(time left " + (timeout - endTime)
				+ " ms) with best move: " + bestMove.toString());
		return bestMove;
	}

	@Override
	public void stateMachineMetaGame(long timeout)
			throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {
		trans_table = new Hashtable<BitSet, Transposition>();

		// ABSearch search = new ABSearch(getStateMachine(), getRole());
		/*
		 * Move bestMove = null; System.out.println("starting the search at depth 1");
		 * try { for (int d = 1; d < 20; d++) { System.out.println("search at depth " +
		 * d); bestMove = minMaxSearch((BitSetMachineState)
		 * getStateMachine().getInitialState(), d, timeout);
		 *
		 * } } catch (TimeOutException e) { System.out.println("timeout exception"); }
		 * System.out.println(bestMove.toString());
		 */

	}

	@Override
	public DetailPanel getDetailPanel() {
		return new EmptyDetailPanel();
	}


	public Move minMaxSearch(BitSetMachineState state, int d, long timeout) throws TimeOutException {
		Move bestMove = null;

		int bestValue = Integer.MIN_VALUE;
		int value;
		try {
			for (Move move : getStateMachine().getLegalMoves(state, getRole())) {
				value = 0 - minValue(state, getRole(), move, d, timeout, -100, 0);
				if (value > bestValue) {
					bestValue = value;
					bestMove = move;
				}

			}
		} catch (MoveDefinitionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return bestMove;
	}

	public int miniMax(BitSetMachineState state, int d, long timeout, int alpha, int beta) throws TimeOutException {
		nStates++;
		int alphaOrig = alpha;
		if (System.currentTimeMillis() + 500 >= timeout)
			throw new TimeOutException();
		if (getStateMachine().isTerminal(state)) {
			try {
				return getStateMachine().getGoal(state, getRole());
			} catch (GoalDefinitionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (d == 0) {
			bottomed = true;
			return 42;
		}
		if (trans_table.containsKey(state.state)) {// do something}
			transFound++;
			Transposition trans = trans_table.get(state.state);
			if (trans.d >= d) {
				if (trans.type.equals("EXACT")) {
					return trans.bestValue;
				}
				if (trans.type.equals("LOWERBOUND")) {
					alpha = Math.max(alpha, trans.bestValue);
				}
				if (trans.type.equals("UPPERBOUND")) {
					beta = Math.min(beta, trans.bestValue);
				}
				if (alpha >= beta)
					return trans.bestValue;
			}
		}
		int bestValue = Integer.MIN_VALUE;
		int value;
		Move bmove = null;
		try {
			for (Move move : getStateMachine().getLegalMoves(state, getRole())) {
				value = 0 - minValue(state, getRole(), move, d, timeout, -beta, -alpha);
				if (value > bestValue) {
					bmove = move;
				}
				bestValue = Math.max(value, bestValue);
				if (bestValue > alpha) {
					alpha = bestValue;
					if (alpha >= beta)
						break;
				}
			} // store the state in the transposition table here
			String type = "";
			if (bestValue <= alphaOrig) {
				type = "UPPERBOUND";
			} else {
				if (bestValue >= beta) {
					type = "LOWERBOUND";
				} else {
					type = "EXACT";
				}
			}
			Transposition trans = new Transposition(bestValue, d, type, bmove);
			BitSet bstate = state.state;
 			trans_table.put(bstate, trans);
		} catch (MoveDefinitionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return bestValue;
	}

	private int minValue(BitSetMachineState state, Role mine, Move move, int d, long timeout, int alpha, int beta)

			throws TimeOutException {

		int bestValue = Integer.MIN_VALUE;
		int value;
		try {
			for (List<Move> jointMove : getStateMachine().getLegalJointMoves(state, mine, move)) {
				value = 0 - miniMax((BitSetMachineState) getStateMachine().getNextState(state, jointMove), d - 1,
						timeout, -beta, -alpha);
				bestValue = Math.max(value, bestValue);
				if (bestValue > alpha) {
					alpha = bestValue;
					if (alpha >= beta)
						break;
				}
			}
		} catch (MoveDefinitionException | TransitionDefinitionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bestValue;
	}

	public Move minMaxSearchNoAB(BitSetMachineState state, int d, long timeout) throws TimeOutException {
		Move bestMove = null;

		int bestValue = Integer.MIN_VALUE;
		int value;
		try {
			for (Move move : getStateMachine().getLegalMoves(state, getRole())) {
				value = 0 - minValue(state, getRole(), move, d, timeout);
				if (value > bestValue) {
					bestValue = value;
					bestMove = move;
				}

			}
		} catch (MoveDefinitionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return bestMove;
	}

	public int miniMax(BitSetMachineState state, int d, long timeout) throws TimeOutException {
		nStates++;
		if (System.currentTimeMillis() + 500 >= timeout)
			throw new TimeOutException();
		if (getStateMachine().isTerminal(state)) {
			try {
				return getStateMachine().getGoal(state, getRole());
			} catch (GoalDefinitionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (d == 0) {
			bottomed = true;
			return 42;
		}

		int bestValue = Integer.MIN_VALUE;
		int value;
		try {
			for (Move move : getStateMachine().getLegalMoves(state, getRole())) {
				value = 0 - minValue(state, getRole(), move, d, timeout);
				bestValue = Math.max(value, bestValue);

			}
		} catch (MoveDefinitionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return bestValue;
	}

	private int minValue(BitSetMachineState state, Role mine, Move move, int d, long timeout)

			throws TimeOutException {
		int bestValue = Integer.MIN_VALUE;
		int value;
		try {
			for (List<Move> jointMove : getStateMachine().getLegalJointMoves(state, mine, move)) {
				value = 0 - miniMax((BitSetMachineState) getStateMachine().getNextState(state, jointMove), d - 1,
						timeout);
				bestValue = Math.max(value, bestValue);

			}
		} catch (MoveDefinitionException | TransitionDefinitionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bestValue;
	}

}