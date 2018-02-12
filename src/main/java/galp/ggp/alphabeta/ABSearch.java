package galp.ggp.alphabeta;

import org.ggp.base.util.statemachine.StateMachine;

import galp.ggp.statemachine.BitSetMachineState;
import galp.ggp.statemachine.TrialPropNetStateMachine;

public class ABSearch {

	ABNode root;
	TrialPropNetStateMachine stateMachine;

	public ABSearch(StateMachine stateMachine) {
		this.stateMachine = (TrialPropNetStateMachine)stateMachine;
		root = new ABNode(null, (BitSetMachineState)stateMachine.getInitialState());
	}

}
