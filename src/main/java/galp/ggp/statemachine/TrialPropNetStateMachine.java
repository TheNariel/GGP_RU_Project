package galp.ggp.statemachine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ggp.base.util.gdl.grammar.Gdl;
import org.ggp.base.util.gdl.grammar.GdlSentence;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.SimpleMachineState;
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
	BitSet state;
	private MachineState initialState;
	private ImmutableList<Role> roles;
	PropNetStructure structure = null;
	String gdlFileName = ".//src//main//java//galp//ggp//main//out.gv";

	public TrialPropNetStateMachine() {

	}

	@Override
	public void initialize(List<Gdl> description) {
		PropNetStructureFactory factory = new GGPBasePropNetStructureFactory();

		try {
			structure = factory.create("realySmallGame", description);
			// structure = factory.create("Game", description);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		structure.renderToFile(new File(gdlFileName));
		state = new BitSet(structure.getNbComponents());

		roles = ImmutableList.copyOf(structure.getRoles());
		initialState = computeInitialState();
		List<Move> legals = null;
		try {
			legals = getLegalMoves(initialState, new Role(null));
		} catch (MoveDefinitionException e) {
			e.printStackTrace();
		}

		for (Move s : legals) {
			System.out.println(s.toString());
		}

	}

	private MachineState computeInitialState() {
		Set<GdlSentence> contents = new HashSet<GdlSentence>();

		for (BaseProposition prop : structure.getBasePropositions()) {
			if (prop.initialValue) {
				state.set(prop.id);
				GdlSentence[] sentences = prop.sentences;
				for (int i = 0; i < sentences.length; i++) {
					contents.add(sentences[i]);
				}
			}

		}
		SimpleMachineState ret = new SimpleMachineState(contents);
		return ret;
	}

	@Override
	public int getGoal(MachineState state, Role role) throws GoalDefinitionException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isTerminal(MachineState state) {

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
	public List<Move> getLegalMoves(MachineState s, Role role) throws MoveDefinitionException {
		List<Move> ret = new ArrayList<Move>();
		// PropNetMove[] moves =structure.getPossibleMoves(roles.indexOf(role));
		PropNetMove[] moves = structure.getPossibleMoves(0);
		for (int i = 0; i < moves.length; i++) {

			// System.out.println(moves[i].toString());
			// System.out.println(moves[0].getLegalComponent());
			boolean legal = checkLegality(state, moves[i].getLegalComponent());
			// System.out.println(legal);
			if (legal) {
				ret.add(Move.create(moves[i].toString()));
			}

		}
		return ret;
	}

	@Override
	public MachineState getNextState(MachineState state, List<Move> moves) throws TransitionDefinitionException {

		return null;
	}

	public boolean checkLegality(BitSet s, StaticComponent root) {
		System.out.println("starting to compute legality");
		BitSet currentInternalState = (BitSet) s.clone();
		BitSet seen = new BitSet();
		List<Integer> front = new ArrayList<Integer>();
		int[] inputs;
		int nSeen = 0;
		front.add(root.id);

		while (!front.isEmpty()) {
			StaticComponent current = structure.getComponent(front.get(0));
			System.out.println("current componenet " + current);
			inputs = current.inputs;
			for (int i : inputs) {
				if (!seen.get(i)) {
					front.add(0, i);
				} else {
					nSeen++;
				}
			}
			if (nSeen == current.inputs.length) {
				if (evaluate(current, currentInternalState)) {
					currentInternalState.set(current.id);
				}
				seen.set(current.id);
				front.remove(0);
			}
			nSeen = 0;

		}
		return currentInternalState.get(root.id);
	}

	public static enum Type {
		INIT /* is true in the initial state only */, TRUE, FALSE, BASE /* true(X) */, INPUT /* does(R,M) */, AND, NOT, OR, PIPE /*
																																	 * is
																																	 * essentially
																																	 * an
																																	 * AND
																																	 * (or
																																	 * OR)
																																	 * with
																																	 * a
																																	 * single
																																	 * input
																																	 */
	}

	public is.ru.cadia.ggp.propnet.structure.components.StaticComponent.Type type;

	private boolean evaluate(StaticComponent current, BitSet currentInternalState) {
		boolean ret = false;

		this.type = current.type;
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
