package galp.ggp.main;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.ggp.base.util.game.Game;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

import galp.ggp.statemachine.TrialPropNetStateMachine;
import galp.ggp.statemachine.TrialProverStateMachine;
import is.ru.cadia.ggp.utils.IOUtils;

public class trialMain {

	public static void main(String[] args) throws IOException, MoveDefinitionException, TransitionDefinitionException {
		// setting up the state machine

		String gdlFileName = ".//src//main//java//galp//ggp//main//game.txt";

		//String gdlFileName = ".//src//main//java//galp//ggp//main//realySmallGame.txt";

		String gameDescription = IOUtils.readFile(new File(gdlFileName));
		String preprocessedRules = Game.preprocessRulesheet(gameDescription);
		Game ggpBaseGame = Game.createEphemeralGame(preprocessedRules);

		StateMachine propNetStateMachine = new TrialPropNetStateMachine(); // insert your own machine here
		propNetStateMachine.initialize(ggpBaseGame.getRules());


		StateMachine ProveeStateMachine = new TrialProverStateMachine(); // insert your own machine here
		ProveeStateMachine.initialize(ggpBaseGame.getRules());

		// some testing

		MachineState s0Prop = propNetStateMachine.getInitialState();
		MachineState s0Reas = ProveeStateMachine.getInitialState();

		List<Move> aJointMove = ProveeStateMachine.getLegalJointMoves(s0Prop).get(0);

		MachineState s1Prop = propNetStateMachine.getNextState(s0Prop, aJointMove);
		MachineState s1Reas = ProveeStateMachine.getNextState(s0Reas, aJointMove);


		List<Move> aJointMove2 = ProveeStateMachine.getLegalJointMoves(s1Prop).get(0);

		MachineState s2Prop = propNetStateMachine.getNextState(s1Prop, aJointMove2);
		MachineState s2Reas = ProveeStateMachine.getNextState(s1Reas, aJointMove2);

		System.out.println("Moves");
		for (Move m : aJointMove) {
			System.out.println(m);
		}
		System.out.println("Moves 2");
		for (Move m : aJointMove2) {
			System.out.println(m);
		}

		System.out.println("PropNet part: ");
		System.out.println("s0 "+s0Prop);
		System.out.println("s1 "+s1Prop);
		System.out.println("s2 "+s2Prop);

		System.out.println("Prover part: ");

		System.out.println("s0 "+s0Reas);
		System.out.println("s1 "+s1Reas);
		System.out.println("s2 "+s2Reas);

	}

}
