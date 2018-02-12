package galp.ggp.gamer;

import java.util.List;
import java.util.Random;

import org.ggp.base.apps.player.detail.DetailPanel;
import org.ggp.base.apps.player.detail.EmptyDetailPanel;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

import galp.ggp.alphabeta.ABSearch;

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
		ABSearch search = new ABSearch(getStateMachine());

	}


	@Override
	public DetailPanel getDetailPanel() {
		return new EmptyDetailPanel();
	}

}