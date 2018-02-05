package galp.ggp.main;

import java.io.File;
import java.io.IOException;

import org.ggp.base.util.game.Game;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

import galp.ggp.statemachine.TrialPropNetStateMachine;
import is.ru.cadia.ggp.utils.IOUtils;

public class trialMain {

	public static void main(String[] args) throws IOException, MoveDefinitionException, TransitionDefinitionException {
		// setting up the state machine

		String gdlFileName = ".//src//main//java//galp//ggp//main//game.txt";

		//String gdlFileName = ".//src//main//java//galp//ggp//main//realySmallGame.txt";

		String gameDescription = IOUtils.readFile(new File(gdlFileName));
		String preprocessedRules = Game.preprocessRulesheet(gameDescription);
		Game ggpBaseGame = Game.createEphemeralGame(preprocessedRules);

		StateMachine stateMachine = new TrialPropNetStateMachine(); // insert your own machine here
		//StateMachine stateMachine = new TrialProverStateMachine(); // insert your own machine here

		stateMachine.initialize(ggpBaseGame.getRules());

		// some testing
		/*
		 * MachineState s0 = stateMachine.getInitialState(); List<Move> aJointMove =
		 * stateMachine.getLegalJointMoves(s0).get(0); MachineState s1 =
		 * stateMachine.getNextState(s0, aJointMove);
		 */
	}

}
