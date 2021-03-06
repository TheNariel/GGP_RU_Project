package galp.ggp.gamer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.ggp.base.apps.player.detail.DetailPanel;
import org.ggp.base.apps.player.detail.EmptyDetailPanel;
import org.ggp.base.util.gdl.factory.GdlFactory;
import org.ggp.base.util.gdl.factory.exceptions.GdlFormatException;
import org.ggp.base.util.gdl.grammar.Gdl;
import org.ggp.base.util.gdl.grammar.GdlSentence;
import org.ggp.base.util.gdl.grammar.GdlTerm;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;
import org.ggp.base.util.symbol.factory.exceptions.SymbolFormatException;

import galp.ggp.search.MCSearchGoalDistance;
import galp.ggp.search.Node;
import galp.ggp.statemachine.TrialPropNetStateMachine;

public class MCGamerGoalDistance extends TrialSampleGamer {
	//Debug options
	boolean printRules = false;

	int nStates;
	Node root;
	MCSearchGoalDistance search;
	TrialPropNetStateMachine orginalStateMachine;
	TrialPropNetStateMachine reducedStateMachine;


	@Override
	public Move stateMachineSelectMove(long timeout)
			throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {

		List<GdlTerm> lastMoves = getMatch().getMostRecentMoves();
//		System.out.println("::: LastMoves" + lastMoves);

		if (lastMoves != null) {
			List<Integer> jointMove = new ArrayList<Integer>();
			int r = 0;
			for (GdlTerm sentence : lastMoves) {
				jointMove.add(root.legalActions.get(r).indexOf(getStateMachine().getMoveFromTerm(sentence)));
				r++;
			}
			root = root.exploredChildren.get(jointMove.toString());
			root.parent=null;
		}
		List<GdlSentence> l = new ArrayList<>(getCurrentState().getContents());
		Collections.sort(l, new Comparator<GdlSentence>() {
			@Override
			public int compare(GdlSentence s1, GdlSentence s2) {
				return s1.toString().compareTo(s2.toString());
			}
		});

//		assert(root.state.equals(getCurrentState()));

//		System.out.println("::: currentState " + l);
		if (root == null)
			root = search.initNextNode(getCurrentState(), null, null);
//		System.out.println(":::::::::::::::::::::::::::: LegalMoves - Original state machine");
////		for(Move legalMove: orginalStateMachine.getLegalMoves(root.state, getRole())) {
//			System.out.println(legalMove);
//		}
		MachineState cs = getCurrentState();
		assert(root.state.equals(cs));
		return search.search(root, timeout, getRole());
	}

	@Override
	public void stateMachineMetaGame(long timeout)
			throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {

		// Get original StateMachine
		orginalStateMachine = (TrialPropNetStateMachine) getStateMachine();

		// Copy rules
		List<Gdl> orginalRules = orginalStateMachine.getRules();
		List<Gdl> copiedRules = new ArrayList<Gdl>();
		for(Gdl rule : orginalRules) {
			copiedRules.add(rule);
		}

		// Create trialPropNetStateMachine
		reducedStateMachine = new TrialPropNetStateMachine();

		// Reduce rules
		List<Gdl> reducedRules = null;
		reducedRules = reduceRules(copiedRules);

		//Print rules
		if (printRules) {
			System.out.println(":::: Original Rules");
			for (Gdl rule : orginalRules) {
				System.out.println(rule);
			}
			System.out.println("\n:::: Reduced Rules");
			for (Gdl rule : reducedRules) {
				System.out.println(rule);
			}
		}
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

	public List<Gdl> reduceRules(List<Gdl> orgRules) {
		try {
			List<Gdl> reducedRules = new ArrayList<Gdl>();
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
				reducedRules.add(GdlFactory.create(rule));
			}
			reducedRules.add(GdlFactory.create("( <= ( next ( ?F ) ) ( true ( ?F ) ) )"));
			reducedRules.add(GdlFactory.create("( trueValue )"));
			return reducedRules;
		} catch (GdlFormatException e) {
			e.printStackTrace();
			return null;
		} catch (SymbolFormatException e) {
			e.printStackTrace();
			return null;
		}
	}

}