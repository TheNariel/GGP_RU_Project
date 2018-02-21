package galp.ggp.gamer;

import org.ggp.base.apps.player.detail.DetailPanel;
import org.ggp.base.apps.player.detail.EmptyDetailPanel;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

import galp.ggp.search.MCSearch;
import galp.ggp.search.Node;
import galp.ggp.statemachine.BitSetMachineState;

public class MCGamer extends TrialSampleGamer {
	int nStates;

	@Override
	public Move stateMachineSelectMove(long timeout)
			throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {
		// Geting the initial "root" node for the tree
		MCSearch search = new MCSearch(getStateMachine());
		Node root = search.initNextNode((BitSetMachineState) getCurrentState(), null, null);

		return search.search(root, timeout, getRole());
	}

	@Override
	public void stateMachineMetaGame(long timeout)
			throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {

		System.out.println("::::META GAME START::::");
		/*
		 * Node root = initNextNode((BitSetMachineState) getCurrentState(), null, null);
		 * nStates =0; mcSearch(root, timeout);
		 *
		 * Move bestMove = null; for (int r = 0; r <
		 * getStateMachine().getRoles().size(); r++) { if
		 * (getStateMachine().getRoles().get(r).equals(getRole())) { int i=0; int max =
		 * root.N[r][0]; for (int m = 1; m < root.N[r].length; m++) { if (root.N[r][m] >
		 * max) { max=root.N[r][m]; i=m; } }
		 *
		 * bestMove = root.legalActions.get(r).get(i);
		 *
		 * } } System.out.println(bestMove + " n of simulations: "+nStates);
		 */
		System.out.println("::::META GAME END::::");
	}

	@Override
	public DetailPanel getDetailPanel() {
		return new EmptyDetailPanel();
	}



}