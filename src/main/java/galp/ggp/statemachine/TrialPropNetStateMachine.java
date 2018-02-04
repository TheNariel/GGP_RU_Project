package galp.ggp.statemachine;

import java.io.File;
import java.util.List;

import org.ggp.base.util.gdl.grammar.Gdl;
import org.ggp.base.util.propnet.architecture.PropNet;
import org.ggp.base.util.propnet.factory.OptimizingPropNetFactory;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
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
	private MachineState initialState;
	private ImmutableList<Role> roles;
	private PropNet propNet;
	PropNetStructure structure = null;
	String gdlFileName = "D:\\Projects\\GGP\\base_git\\GGP_RU_Project\\src\\main\\java\\galp\\ggp\\main\\out.txt";

	public TrialPropNetStateMachine() {

	}

	@Override
	public void initialize(List<Gdl> description) {
		System.out.println("TG");
		PropNetStructureFactory factory = new GGPBasePropNetStructureFactory();
		try {
			propNet = OptimizingPropNetFactory.create(description);
			structure = factory.create(description);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("TESTING");
		structure.renderToFile(new File(gdlFileName));
		roles = ImmutableList.copyOf(structure.getRoles());
		initialState = computeInitialState();

	}

	private MachineState computeInitialState() {
		System.out.println(propNet.getInitProposition());
		for (BaseProposition prop : structure.getBasePropositions()) {
			System.out.println(prop);
		}
		return null;
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
		return null;
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
