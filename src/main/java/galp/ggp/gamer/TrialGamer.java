package galp.ggp.gamer;

import java.util.List;
import java.util.Random;

import org.ggp.base.apps.player.detail.DetailPanel;
import org.ggp.base.apps.player.detail.EmptyDetailPanel;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

import galp.ggp.statemachine.BitSetMachineState;
import galp.ggp.statemachine.TimeOutException;

public class TrialGamer extends TrialSampleGamer {

	@Override
	public Move stateMachineSelectMove(long timeout)
			throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {

		Random rng = new Random();
		StateMachine theMachine = getStateMachine();
		List<Move> moves = theMachine.getLegalMoves(getCurrentState(), getRole());

		Move selection = moves.get(rng.nextInt(moves.size()));
		return selection;
	}

	@Override
	public void stateMachineMetaGame(long timeout)
			throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {
		// ABSearch search = new ABSearch(getStateMachine(), getRole());
		try {
			for (int d = 1; d < 100; d++) {

				minMaxSearch((BitSetMachineState) getStateMachine().getInitialState(), d, timeout);

			}
		} catch (TimeOutException e) {

		}

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
		if (d == 0)
			return 42;
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

	private int minValue(BitSetMachineState state, Role mine, Move move, int d, long timeout) throws TimeOutException {
		int bestValue = Integer.MIN_VALUE;
		int value;
		try {
			for (List<Move> jointMove : getStateMachine().getLegalJointMoves(state, mine, move)) {
				value = 0
						- miniMax((BitSetMachineState) getStateMachine().getNextState(state, jointMove), d - 1, timeout);
				bestValue = Math.max(value, bestValue);
			}
		} catch (MoveDefinitionException | TransitionDefinitionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bestValue;
	}
}