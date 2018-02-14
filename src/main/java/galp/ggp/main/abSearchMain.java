package galp.ggp.main;

import java.io.File;
import java.io.IOException;

import org.ggp.base.player.gamer.statemachine.sample.SampleSearchLightGamer;
import org.ggp.base.util.game.Game;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;
import org.ggp.base.util.statemachine.implementation.prover.ProverStateMachine;

import is.ru.cadia.ggp.utils.IOUtils;

public class abSearchMain {

	public static void main(String[] args) throws IOException, MoveDefinitionException, TransitionDefinitionException {
		// String gdlFileName = ".//src//main//java//galp//ggp//main//game.txt";
//		String gdlFileName = ".//src//main//java//galp//ggp//main//bidding-tictactoe.gdl.txt";
		String gdlFileName = ".//src//main//java//galp//ggp//main//realySmallGame.txt";
		String gameDescription = IOUtils.readFile(new File(gdlFileName));
		String preprocessedRules = Game.preprocessRulesheet(gameDescription);
		Game ggpBaseGame = Game.createEphemeralGame(preprocessedRules);

		StateMachine proverStateMachine = new ProverStateMachine(); // insert your own machine here
		proverStateMachine.initialize(ggpBaseGame.getRules());

		MachineState state = proverStateMachine.getInitialState();
		System.out.println(proverStateMachine.getLegalJointMoves(state).toString());

		SampleSearchLightGamer sampleSearchLightGamer = new SampleSearchLightGamer();

//		MinMaxGamer minMaxGamer = new MinMaxGamer();
//		minMaxGamer.metaGame(1618578470735L);
//		TrialGamer myTrialGamer = new TrialGamer();
//		myTrialGamer.switchStateMachine(proverStateMachine);
//		myTrialGamer.stateMachineSelectMove(999L);


//		simulateGame(proverStateMachine);
	}

}
