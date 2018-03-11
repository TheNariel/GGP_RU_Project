package galp.ggp.gamer;

import java.util.ArrayList;
import java.util.List;

import org.ggp.base.apps.player.detail.DetailPanel;
import org.ggp.base.apps.player.detail.EmptyDetailPanel;
import org.ggp.base.util.gdl.factory.GdlFactory;
import org.ggp.base.util.gdl.factory.exceptions.GdlFormatException;
import org.ggp.base.util.gdl.grammar.Gdl;
import org.ggp.base.util.gdl.grammar.GdlTerm;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;
import org.ggp.base.util.symbol.factory.exceptions.SymbolFormatException;

import galp.ggp.search.MCSearchGoalDistance;
import galp.ggp.search.Node;
import galp.ggp.statemachine.TrialPropNetStateMachine;

public class MCGamerGoalDistanceHeuristics extends TrialSampleGamer {
	int nStates;
	Node root;
	MCSearchGoalDistance search;
	TrialPropNetStateMachine orginalStateMachine;
	TrialPropNetStateMachine reducedStateMachine;

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

		}
		if (root == null)
			root = search.initNextNode(getCurrentState(), null, null);
		return search.search(root, timeout, getRole());
	}

	@Override
	public void stateMachineMetaGame(long timeout)
			throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {
		// Get orginal StateMachine
		orginalStateMachine = (TrialPropNetStateMachine) getStateMachine();
		// Create trialPropNetStateMachine
		reducedStateMachine = new TrialPropNetStateMachine();
		// Filter the rules
		List<Gdl> rules = orginalStateMachine.getRules();
		System.out.println("::::Rules");
		for (Gdl rule: rules) {
			System.out.println(rule);
		}
		List<Gdl> reducedRules = filterRules(orginalStateMachine.getRules());
		// Initialize StateMachine
		reducedStateMachine.initialize(reducedRules);

		System.out.println("::::META GAME START::::");
		search = new MCSearchGoalDistance(orginalStateMachine, reducedStateMachine);

		root = search.initNextNode(getStateMachine().getInitialState(), null, null);
		search.search(root, timeout, getRole());
		System.out.println("::::META GAME END::::");



	}

	@Override
	public DetailPanel getDetailPanel() {
		return new EmptyDetailPanel();
	}

	public List<Gdl> filterRules(List<Gdl> orgRules) {
		try {
			List<Gdl> filteredRules = new ArrayList<Gdl>();
			String rule;
			for (int i = 0; i < orgRules.size(); i++) {
				rule = orgRules.get(i).toString();
				// replace (not x) with trueValue
				// and replace (not (x)) with trueValue
				String patternNot = "\\( not [a-zA-Z ]*\\)";
				String patternNotWParenth = "\\([ ]*not[ ]*\\([a-zA-Z ]*\\)[ ]*\\)";
				rule = rule.replaceAll(patternNot, "( trueValue )");
				rule = rule.replaceAll(patternNotWParenth, "( trueValue )");

				// replace does with legal
				rule = rule.replaceAll("does", "legal");
				filteredRules.add(GdlFactory.create(rule));
			}
			return filteredRules;
		} catch (GdlFormatException e) {
			e.printStackTrace();
			return null;
		} catch (SymbolFormatException e) {
			e.printStackTrace();
			return null;
		}
	}


}