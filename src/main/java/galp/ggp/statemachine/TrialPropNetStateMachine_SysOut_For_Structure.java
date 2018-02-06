package galp.ggp.statemachine;

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

public class TrialPropNetStateMachine_SysOut_For_Structure extends StateMachine {
	BitSet state;
	private MachineState initialState;
	private ImmutableList<Role> roles;
	PropNetStructure structure = null;
	String gdlFileName = ".//src//main//java//galp//ggp//main//out.txt";

	public TrialPropNetStateMachine_SysOut_For_Structure() {

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
		state = new BitSet(structure.getNbComponents());

		roles = ImmutableList.copyOf(structure.getRoles());
		initialState = computeInitialState();

//		structure.renderToFile(new File(gdlFileName));

		System.out.println("\n--------INITIALIZE POKING PROCEDURE--------");
		System.out.println("State:");
		System.out.println(state);

		System.out.println("\ngetNbBasePropositions()");
		System.out.println(structure.getNbBasePropositions());

		System.out.println("\ngetBasePropositions()");
		BaseProposition[] baseProp = structure.getBasePropositions();
		for (BaseProposition prop : baseProp) {
			System.out.println(prop);
		}

//		System.out.println("\ngetRoleId(Role role)");
//		System.out.println(structure.getRoleId(Role "robot"));

		System.out.println("\ngetGoalPropositions(int roleId)");
		StaticComponent[] goalProp = structure.getGoalPropositions(0);
		for (StaticComponent prop : goalProp) {
			System.out.println(prop);
		}

		System.out.println("\ngetGoalValues(int roleId)");
		int[] goalValues = structure.getGoalValues(0);
		for (int i : goalValues) {
			System.out.println(i);
		}
		System.out.println(structure.getGoalValues(0));

		System.out.println("\ngetTerminalProposition()");
		System.out.println(structure.getTerminalProposition());

		System.out.println("\ngetPossibleMoves(int roleId)");
		PropNetMove[] possibleMoves = structure.getPossibleMoves(0);
		for (int i = 0; i < possibleMoves.length; i++) {
			System.out.println(possibleMoves[i]);
		}

//		System.out.println("\ngetPropNetMove(int rid, Move m)");
//		System.out.println(structure.getPropNetMove(int rid, Move m));

//		System.out.println("\ngetBaseProposition(GdlSentence sentence)");
//		System.out.println(structure.getBaseProposition(GdlSentence sentence));
//
		System.out.println("\ngetNbComponents()");
		System.out.println(structure.getNbComponents());

		System.out.println("\ngetComponents()");
		System.out.println(structure.getComponents());


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
