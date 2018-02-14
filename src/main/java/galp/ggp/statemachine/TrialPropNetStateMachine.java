package galp.ggp.statemachine;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.ggp.base.util.gdl.grammar.Gdl;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

import com.google.common.collect.ImmutableList;

import is.ru.cadia.ggp.propnet.PropNetMove;
import is.ru.cadia.ggp.propnet.structure.GGPBasePropNetStructureFactory;
import is.ru.cadia.ggp.propnet.structure.PropNetStructure;
import is.ru.cadia.ggp.propnet.structure.PropNetStructureFactory;
import is.ru.cadia.ggp.propnet.structure.components.BaseProposition;
import is.ru.cadia.ggp.propnet.structure.components.StaticComponent;

public class TrialPropNetStateMachine extends StateMachine {
	private BitSetMachineState initialState;
	private ImmutableList<Role> roles;
	PropNetStructure structure = null;
	public PropNetStructure getStructure() {
		return structure;
	}

	String gdlFileName = ".//src//main//java//galp//ggp//main//out.gv";

	public TrialPropNetStateMachine() {

	}

	@Override
	public void initialize(List<Gdl> description) {
		System.out.println("get init propnet");
		PropNetStructureFactory factory = new GGPBasePropNetStructureFactory();

		try {
			structure = factory.create(description);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		roles = ImmutableList.copyOf(structure.getRoles());
		initialState = (BitSetMachineState) computeInitialState();
	}

	private MachineState computeInitialState() {
		BitSet state = new BitSet(structure.getNbComponents());

		for (BaseProposition prop : structure.getBasePropositions()) {
			if (prop.initialValue) {
				state.set(prop.id);

			}

		}
		MachineState ret = new BitSetMachineState(state, structure);
		return ret;
	}

	@Override
	public int getGoal(MachineState state, Role role) throws GoalDefinitionException {
		int roleid = structure.getRoleId(role);
		StaticComponent[] Goals = structure.getGoalPropositions(roleid);
		for(int i = 0; i < Goals.length;i++) {
			boolean legal = checkLegality(state,Goals[i]);
			if(legal) {return structure.getGoalValues(roleid)[i];}
		}
		return 0;
	}

	@Override
	public boolean isTerminal(MachineState state) {
		StaticComponent Term = structure.getTerminalProposition();
		boolean terminal = checkLegality(state,Term);
		if(terminal) return true;

		return false;
	}

	@Override
	public List<Role> getRoles() {
		return roles;
	}

	@Override
	public MachineState getInitialState() {
		return initialState;
	}

	@Override
	public List<Move> getLegalMoves(MachineState state, Role role) throws MoveDefinitionException {
		List<Move> ret = new ArrayList<Move>();
		PropNetMove[] moves = structure.getPossibleMoves(roles.indexOf(role));

		BitSetMachineState currentState = (BitSetMachineState) state;
		currentState.seen.clear();
		for (int i = 0; i < moves.length; i++) {

			boolean legal = checkLegality(state, moves[i].getLegalComponent());
			if (legal) {
				ret.add(Move.create(moves[i].toString()));
			}

		}
		return ret;
	}

	@Override
	public MachineState getNextState(MachineState state, List<Move> moves) throws TransitionDefinitionException {

		int r =0;
		BitSetMachineState currentState = (BitSetMachineState) state.clone();
		for(Move m : moves) {
			PropNetMove pnm = structure.getPropNetMove(roles.get(r), m);
			r++;
			currentState.state.set(pnm.getInputComponent().id);
		}

		BitSet nextState = new BitSet();
		for (BaseProposition prop : structure.getBasePropositions()) {

			if(checkLegality(currentState,  prop.nextComponent)) {
				nextState.set(prop.id);
			}

		}
		currentState.state=(BitSet) nextState.clone();
		return currentState;
	}

	public boolean checkLegality(MachineState state, StaticComponent root) {

		BitSetMachineState currentState = (BitSetMachineState) state;

		List<Integer> front = new ArrayList<Integer>();
		int[] inputs;
		int nSeen = 0;
		front.add(root.id);

		while (!front.isEmpty()) {
			StaticComponent current = structure.getComponent(front.get(0));
			inputs = current.inputs;
			for (int i : inputs) {
				if (!currentState.seen.get(i)) {
					front.add(0, i);
				} else {
					nSeen++;
				}
			}
			if (nSeen == current.inputs.length) {
				if (evaluate(current, currentState.state)) {
					currentState.state.set(current.id);
				}
				currentState.seen.set(current.id);
				front.remove(0);
			}
			nSeen = 0;

		}
		return currentState.state.get(root.id);
	}


	private boolean evaluate(StaticComponent current, BitSet currentInternalState) {
		boolean ret = false;

		is.ru.cadia.ggp.propnet.structure.components.StaticComponent.Type type;
		type = current.type;
		switch (type) {
		case AND:
			ret = true;
			for (int i : current.inputs) {
				ret = ret && currentInternalState.get(i);
			}
			break;
		case OR:
			for (int i : current.inputs) {
				ret = ret || currentInternalState.get(i);
			}
			break;
		case NOT:
			ret = !currentInternalState.get(current.inputs[0]);
			break;
		case PIPE:
			ret = currentInternalState.get(current.id);
			break;
		case TRUE:
			ret = true;
			break;
		case FALSE:
			ret = false;
			break;
		case INPUT:
			ret = currentInternalState.get(current.id);
			break;
		case BASE:
			ret = currentInternalState.get(current.id);
			break;
		default:
			break;
		}
		return ret;
	}

}
