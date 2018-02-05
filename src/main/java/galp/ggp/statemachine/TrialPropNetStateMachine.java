package galp.ggp.statemachine;

import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;
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

import is.ru.cadia.ggp.propnet.structure.GGPBasePropNetStructureFactory;
import is.ru.cadia.ggp.propnet.structure.PropNetStructure;
import is.ru.cadia.ggp.propnet.structure.PropNetStructureFactory;
import is.ru.cadia.ggp.propnet.structure.components.BaseProposition;

public class TrialPropNetStateMachine extends StateMachine {
	BitSet state;
	private MachineState initialState;
	private ImmutableList<Role> roles;
	PropNetStructure structure = null;
	String gdlFileName = ".//src//main//java//galp//ggp//main//out.txt";

	public TrialPropNetStateMachine() {

	}

	@Override
	public void initialize(List<Gdl> description) {
		PropNetStructureFactory factory = new GGPBasePropNetStructureFactory();
		try {
			structure = factory.create(description);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		roles = ImmutableList.copyOf(structure.getRoles());
		initialState = computeInitialState();
		state = new BitSet(structure.getNbComponents());

		// structure.renderToFile(new File(gdlFileName));
	}

	private MachineState computeInitialState() {
		Set<GdlSentence> contents = new Set<GdlSentence>() {

			@Override
			public <T> T[] toArray(T[] arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Object[] toArray() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int size() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public boolean retainAll(Collection<?> arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean removeAll(Collection<?> arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean remove(Object arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public Iterator<GdlSentence> iterator() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean isEmpty() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean containsAll(Collection<?> arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean contains(Object arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void clear() {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean addAll(Collection<? extends GdlSentence> arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean add(GdlSentence arg0) {
				// TODO Auto-generated method stub
				return false;
			}
		};

		for (BaseProposition prop : structure.getBasePropositions()) {
			if (prop.initialValue) {
				GdlSentence[] sentences = prop.sentences;
//todo initial state.
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Role> getRoles() {
		// TODO Auto-generated method stub
		return roles;
	}

	@Override
	public MachineState getInitialState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Move> getLegalMoves(MachineState state, Role role) throws MoveDefinitionException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MachineState getNextState(MachineState state, List<Move> moves) throws TransitionDefinitionException {
		// TODO Auto-generated method stub
		return null;
	}

}
