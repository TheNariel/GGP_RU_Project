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

import galp.ggp.search.MCSearchCutoff;
import galp.ggp.search.Node;

public class MCGamerCT extends TrialSampleGamer {
	int nStates;
	Node root;
	MCSearchCutoff search;

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
			}
			root = root.exploredChildren.get(jointMove.toString());
			//fix for a "problem" need to copy it to all other Mc based gamers
			root.parent=null;
		}
		if (root == null)
			root = search.initNextNode(getCurrentState(), null, null);
		return search.search(root, timeout, getRole());
	}

	@Override
	public void stateMachineMetaGame(long timeout)
			throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {

		System.out.println("::::META GAME START::::");
		search = new MCSearchCutoff(getStateMachine());

		root = search.initNextNode(getStateMachine().getInitialState(), null, null);
		search.search(root, timeout, getRole());
		System.out.println("::::META GAME END::::");
	}

	@Override
	public DetailPanel getDetailPanel() {
		return new EmptyDetailPanel();
	}

}