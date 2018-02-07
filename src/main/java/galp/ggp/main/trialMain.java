package galp.ggp.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Random;

import org.ggp.base.util.game.Game;
import org.ggp.base.util.logging.GamerLogger;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;
import org.ggp.base.util.statemachine.implementation.prover.ProverStateMachine;

import galp.ggp.statemachine.TrialPropNetStateMachine;
import is.ru.cadia.ggp.utils.IOUtils;

public class trialMain {

	public static void main(String[] args) throws IOException, MoveDefinitionException, TransitionDefinitionException {
		// setting up the state machine

		// String gdlFileName = ".//src//main//java//galp//ggp//main//game.txt";

		String gdlFileName = ".//src//main//java//galp//ggp//main//bidding-tictactoe.gdl.txt";

		String gameDescription = IOUtils.readFile(new File(gdlFileName));
		String preprocessedRules = Game.preprocessRulesheet(gameDescription);
		Game ggpBaseGame = Game.createEphemeralGame(preprocessedRules);

		StateMachine propNetStateMachine = new TrialPropNetStateMachine(); // insert your own machine here
		propNetStateMachine.initialize(ggpBaseGame.getRules());

		Long starTime = System.currentTimeMillis();
		int nGameSteps = 0;
		int nOfGames = 1000;
		for (int i = 0; i < nOfGames; i++) {
			nGameSteps += simulateGame(propNetStateMachine);
		}
		System.out.println();
		Long endTime = System.currentTimeMillis();
		String propnetStats = "#game Steps: " + nGameSteps + " took :" + (endTime - starTime) / 1000.0
				+ "s, average time per game step: " + ((float) (endTime - starTime)) / nGameSteps + " ms";
		System.out.println(propnetStats);

		propNetStateMachine = new ProverStateMachine(); // insert your own machine here
		propNetStateMachine.initialize(ggpBaseGame.getRules());

		System.out.println("reasoner:");
		starTime = System.currentTimeMillis();
		nGameSteps = 0;
		for (int i = 0; i < nOfGames; i++) {
			nGameSteps += simulateGame(propNetStateMachine);
		}
		System.out.println();
		endTime = System.currentTimeMillis();
		String proverStats = "#game Steps: " + nGameSteps + " took :" + (endTime - starTime) / 1000.0
				+ "s, average time per game step: " + ((float) (endTime - starTime)) / nGameSteps + " ms";
		System.out.println(proverStats);
		String outputName = ".//src//main//java//galp//ggp//main//stats.txt";
		try {
			FileOutputStream fos = new FileOutputStream(new File(outputName), true);
			OutputStreamWriter fout = new OutputStreamWriter(fos, "UTF-8");
			fout.write("GAME: run " + nOfGames + " times \n");
			fout.write(gdlFileName + "\n");
			fout.write("propNet: \n");
			fout.write(propnetStats + "\n");
			fout.write("Prover: \n");
			fout.write(proverStats + "\n\n");
			fout.close();
			fos.close();
		} catch (Exception e) {
			GamerLogger.logStackTrace("StateMachine", e);
		}
	}

	public static int simulateGame(StateMachine propNetStateMachine)
			throws MoveDefinitionException, TransitionDefinitionException {
		int ret = 0;
		Random rng = new Random();
		MachineState state = propNetStateMachine.getInitialState();
		List<List<Move>> moves;
		List<Move> move;

		// System.out.println("Initial state " + state);
		while (!propNetStateMachine.isTerminal(state)) {

			moves = propNetStateMachine.getLegalJointMoves(state);
			move = moves.get(rng.nextInt(moves.size()));

			/*
			 * System.out.print("Joint Move"); for (Move m : move) { System.out.print(" " +
			 * m + " "); } System.out.println();
			 */

			state = propNetStateMachine.getNextState(state, move);
			// System.out.println("state after move " + state);
			ret++;
		}
		/*try {
			System.out.print(propNetStateMachine.getGoal(state, propNetStateMachine.getRoles().get(0)) + " ");
		} catch (GoalDefinitionException e) {
			e.printStackTrace();
		}*/
		// System.out.println("Game ended in terminal state");
		return ret;
	}
}
