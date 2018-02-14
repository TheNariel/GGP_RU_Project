/**
 *
 */
package galp.ggp.gamer;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.ggp.base.player.gamer.statemachine.sample.SampleGamer;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

import galp.ggp.statemachine.BitSetMachineState;
import galp.ggp.statemachine.TimeOutException;

/**
 * @author frimann
 *
 */
public class MinMaxGamer extends SampleGamer {
	private Random randomGenerator;
	public Logger selectMoveLogger;
    FileHandler smfh;

	public MinMaxGamer() {
		selectMoveLogger = Logger.getLogger("selectMove");
	    try {
	        // This block configure the logger with handler and formatter
	        smfh = new FileHandler("selectMove.log");
	//        logger.addHandler(smfh);
	        SimpleFormatter formatter = new SimpleFormatter();
	        smfh.setFormatter(formatter);
	        // the following statement is used to log any messages
	  //      logger.info("My first log";
	    } catch (SecurityException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	@Override
	public void stateMachineMetaGame(long timeout)
			throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {
		Logger logger = Logger.getLogger("MyLog");
	    FileHandler fh;

//	    try {
//	        // This block configure the logger with handler and formatter
//	        fh = new FileHandler("MyLogFile.log");
//	        logger.addHandler(fh);
//	        SimpleFormatter formatter = new SimpleFormatter();
//	        fh.setFormatter(formatter);
//	        // the following statement is used to log any messages
//	        logger.info("My first log");
//	    } catch (SecurityException e) {
//	        e.printStackTrace();
//	    } catch (IOException e) {
//	        e.printStackTrace();
//	    }
//
//	// ABSearch search = new ABSearch(getStateMachine(), getRole());
//		try {
//			for (int d = 1; d < 100; d++) {
//				logger.info("d = " + d);
//				miniMax((BitSetMachineState) getStateMachine().getInitialState(), d, timeout);
//
//			}
//		} catch (TimeOutException e) {
//		}

	}

	@Override

	public Move stateMachineSelectMove(long timeout)
			throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {

		Logger logger = Logger.getLogger("selectMove");
	    FileHandler fh;
	    try {
	        // This block configure the logger with handler and formatter
	        fh = new FileHandler("selectMove.log");
	        logger.addHandler(fh);
	        SimpleFormatter formatter = new SimpleFormatter();
	        fh.setFormatter(formatter);
	        // the following statement is used to log any messages
	        logger.info("My first log, timeout = " + timeout);
	    } catch (SecurityException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }

//	    logger.info("Searching");
//    		Move selection = minMaxSearch((BitSetMachineState) getStateMachine().getInitialState(), 1, timeout);
//	    logger.info("found move: " + selection.toString());
//	    return selection;

		List<Move> moves = getStateMachine().getLegalMoves(getCurrentState(), getRole());
        int index = randomGenerator.nextInt(moves.size());
        Move selection = moves.get(index);
        return selection;
	}

	public Move minMaxSearch(BitSetMachineState state, int d, long timeout) throws TimeOutException {
		Move bestMove = null;

		int bestValue = Integer.MIN_VALUE;
		int value;
		try {
			for (Move move : getStateMachine().getLegalMoves(state, getRole())) {
				value = 0 - minValue(state, getRole(), move, d, timeout);
				if (value > bestValue) {
					bestValue = value;
					bestMove = move;
				}

			}
		} catch (MoveDefinitionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return bestMove;
	}

	public int miniMax(BitSetMachineState state, int d, long timeout) throws TimeOutException {
		if (System.currentTimeMillis() + 500 >= timeout)
			throw new TimeOutException();
		if (getStateMachine().isTerminal(state)) {
			try {
				return getStateMachine().getGoal(state, getRole());
			} catch (GoalDefinitionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (d == 0)
			return 40;
		int bestValue = Integer.MIN_VALUE;
		int value;
		try {
			for (Move move : getStateMachine().getLegalMoves(state, getRole())) {
				value = 0 - minValue(state, getRole(), move, d, timeout);
				bestValue = Math.max(value, bestValue);
			}
		} catch (MoveDefinitionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return bestValue;
	}

	private int minValue(BitSetMachineState state, Role mine, Move move, int d, long timeout) throws TimeOutException {
		int bestValue = Integer.MAX_VALUE;
		int value;
		try {
			for (List<Move> joinMove : getStateMachine().getLegalJointMoves(state, mine, move)) {
				value = 0
						- miniMax((BitSetMachineState) getStateMachine().getNextState(state, joinMove), d - 1, timeout);
				bestValue = Math.max(value, bestValue);
			}
		} catch (MoveDefinitionException | TransitionDefinitionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bestValue;
	}
}
