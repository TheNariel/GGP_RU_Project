package galp.ggp.main;

import java.io.File;
import java.io.IOException;

import org.ggp.base.util.game.Game;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

import galp.ggp.search.MCSearch;
import galp.ggp.search.Node;
import galp.ggp.statemachine.BitSetMachineState;
import galp.ggp.statemachine.TrialPropNetStateMachine;
import is.ru.cadia.ggp.utils.IOUtils;

public class trialMain {

	public static void main(String[] args) throws IOException, MoveDefinitionException, TransitionDefinitionException, GoalDefinitionException {
		// setting up the state machine

		// String gdlFileName = ".//src//main//java//galp//ggp//main//game.txt";

		String gdlFileName = ".//src//main//java//galp//ggp//main//realySmallGame.txt";

		String gameDescription = IOUtils.readFile(new File(gdlFileName));
		String preprocessedRules = Game.preprocessRulesheet(gameDescription);
		Game ggpBaseGame = Game.createEphemeralGame(preprocessedRules);

		StateMachine propNetStateMachine = new TrialPropNetStateMachine(); // insert your own machine here
		propNetStateMachine.initialize(ggpBaseGame.getRules());

		MCSearch search = new MCSearch(propNetStateMachine);
		Node root = search.initNextNode((BitSetMachineState) propNetStateMachine.getInitialState(), null, null);

		Move bestMove;
		bestMove = search.search(root, System.currentTimeMillis() +5000, propNetStateMachine.getRoles().get(0));
		System.out.println(bestMove);

	}


}
