package galp.ggp.gamer;

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
	int nStates;

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
		System.out.println("starting the search at depth 1");
		try {
			for (int d = 1; d < 100; d++) {
				nStates = 0;
				bottomed = false;
				System.out.println("search at depth " + d);
				bestMove = minMaxSearch((BitSetMachineState) getCurrentState(), d, timeout);
				//bestMove = minMaxSearchNoAB((BitSetMachineState) getCurrentState(), d, timeout);
				System.out.println(nStates);
				if (!bottomed)
					break;
			}
		} catch (TimeOutException e) {
			System.out.println("timeout exception");
		}
		System.out.println(bestMove.toString());

		return bestMove;
	}

	@Override
	public void stateMachineMetaGame(long timeout)
			throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {
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
				value = 0 - minValue(state, getRole(), move, d, timeout, -beta, -alpha);
				bestValue = Math.max(value, bestValue);
				if (bestValue > alpha) {
					alpha = bestValue;
					if (alpha >= beta)
						break;
				}
			}
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