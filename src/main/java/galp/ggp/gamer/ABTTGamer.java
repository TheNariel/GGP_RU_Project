package galp.ggp.gamer;

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

public class ABTTGamer extends TrialSampleGamer {
	private boolean bottomed;
	int nStates, transFound;

	Hashtable<String, Transposition> trans_table;

	@Override
	public Move stateMachineSelectMove(long timeout)
			throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {
		nStates = 0;
		Move bestMove = null;
		Long startTime = System.currentTimeMillis();

		System.out.println("starting the search at depth 1");
		try {
			// Iterative deepening from 1 to 1000 (unlikely to actually get pass like 15)
			// with break after the whole tree was explored.
			for (int d = 1; d < 1000; d++) {
				nStates = 0;
				transFound = 0;
				bottomed = false;

				System.out.println("search at depth " + d);

				Long startLoopTime = System.currentTimeMillis();
				// staring Search
				bestMove = getBestMoveABTT((BitSetMachineState) getCurrentState(), d, timeout);
				Long endLoopTime = System.currentTimeMillis();

				System.out.println("Depth searched done in: " + (endLoopTime - startLoopTime) + " ms and " + nStates
						+ " nodes and " + transFound + " transpositions found, size of the table: "
						+ trans_table.size());

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
		trans_table = new Hashtable<String, Transposition>();
		nStates = 0;
		Move bestMove = null;
		Long startTime = System.currentTimeMillis();

		System.out.println("::::META GAME START::::");
		System.out.println("starting the search at depth 1");
		try {
			// Iterative deepening from 1 to 1000 (unlikely to actually get pass like 15)
			// with break after the whole tree was explored.
			for (int d = 1; d < 1000; d++) {

				nStates = 0;
				transFound = 0;
				bottomed = false;
				System.out.println("search at depth " + d);

				Long startLoopTime = System.currentTimeMillis();
				// staring Search
				bestMove = getBestMoveABTT((BitSetMachineState) getCurrentState(), d, timeout);
				Long endLoopTime = System.currentTimeMillis();

				System.out.println("Depth searched done in: " + (endLoopTime - startLoopTime) + " ms and " + nStates
						+ " nodes and " + transFound + " transpositions found, size of the table: "
						+ trans_table.size());

				if (!bottomed)
					break;
			}

		} catch (TimeOutException e) {
			System.out.println("no more time, Get out, out, out .... ");
		}
		Long endTime = System.currentTimeMillis();
		System.out.println("Search done in: " + (endTime - startTime) + " ms(time left " + (timeout - endTime)
				+ " ms) with best move: " + bestMove.toString());

		System.out.println("::::META GAME END::::");
	}

	@Override
	public DetailPanel getDetailPanel() {
		return new EmptyDetailPanel();
	}

	// First layer of search, return actual best move (not value), gets called only
	// once per search.
	public Move getBestMoveABTT(BitSetMachineState state, int d, long timeout) throws TimeOutException {
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
			e.printStackTrace();
		}

		return bestMove;
	}

	// function handling nodes in a tree representing our gamers moves.
	public int miniMax(BitSetMachineState state, int d, long timeout, int alpha, int beta) throws TimeOutException {
		nStates++;
		int alphaOrig = alpha;
		int bestValue = Integer.MIN_VALUE;
		int value;
		Move bmove = null;
		String type = "";

		// checking if we still have some time for calculations, if not escape the
		// search immediately.
		if (System.currentTimeMillis() + 100 >= timeout)
			throw new TimeOutException();

		// checking if the state is terminal, if so return its value.
		if (getStateMachine().isTerminal(state)) {
			try {
				return getStateMachine().getGoal(state, getRole());
			} catch (GoalDefinitionException e) {
				e.printStackTrace();
			}
		}

		//depth check, part of the iterative deepening, if depth runs out and we are not in terminal state, just return some value (lower than 50).
		if (d == 0) {
			bottomed = true;
			return 42;
		}

		//looking up state in transposition table, if the state is found than compare its depth and other informations.
		if (trans_table.containsKey(state.getContents().toString())) {
			transFound++;
			Transposition trans = trans_table.get(state.getContents().toString());
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

		try {
			//Getting the values of nodes children
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
			}
			//string state in transposition table, using the Set<GdlSentence> of getBasePropositions (true ones) as a key and storing bestValue,depth,type and the best move.
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
			trans_table.put(state.getContents().toString(), trans);

		} catch (MoveDefinitionException e) {
			e.printStackTrace();
		}

		return bestValue;
	}

	// function handling nodes in a tree representing opponents gamers moves
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
			e.printStackTrace();
		}
		return bestValue;
	}

}