package galp.ggp.gamer;

import java.util.ArrayList;
import java.util.List;

import org.ggp.base.apps.player.detail.DetailPanel;
import org.ggp.base.apps.player.detail.EmptyDetailPanel;
import org.ggp.base.util.gdl.grammar.GdlTerm;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

import galp.ggp.search.MCSearch;
import galp.ggp.search.Node;
import galp.ggp.statemachine.BitSetMachineState;

public class MCGamer extends TrialSampleGamer {
	int nStates;
	Node root;
	MCSearch search;

	@Override
	public Move stateMachineSelectMove(long timeout)
			throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {

		List<GdlTerm> lastMoves = getMatch().getMostRecentMoves();
		if (lastMoves != null) {
			List<Integer> jointMove = new ArrayList<Integer>();
			int r = 0;
			for (GdlTerm sentence : lastMoves) {
				jointMove.add(root.legalActions.get(r).indexOf(getStateMachine().getMoveFromTerm(sentence)));
				r++;
			} //
				// System.out.println(jointMove);

			root = root.next.get(jointMove.toString());

		} /*
			 * else { root = search.initNextNode((BitSetMachineState) getCurrentState(),
			 * null, null); }
			 */
		if (root == null)
			root = search.initNextNode((BitSetMachineState) getCurrentState(), null, null);
		return search.search(root, timeout, getRole());
	}

	@Override
	public void stateMachineMetaGame(long timeout)
			throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {

		System.out.println("::::META GAME START::::");
		search = new MCSearch(getStateMachine());

		root = search.initNextNode((BitSetMachineState) getStateMachine().getInitialState(), null, null);
		search.search(root, timeout, getRole());
		System.out.println("::::META GAME END::::");
	}

	@Override
	public DetailPanel getDetailPanel() {
		return new EmptyDetailPanel();
	}

}