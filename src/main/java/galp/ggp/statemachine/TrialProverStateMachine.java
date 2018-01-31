package galp.ggp.statemachine;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ggp.base.util.gdl.grammar.Gdl;
import org.ggp.base.util.gdl.grammar.GdlSentence;
import org.ggp.base.util.prover.Prover;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;
import org.ggp.base.util.statemachine.implementation.prover.query.ProverQueryBuilder;
import org.ggp.base.util.statemachine.implementation.prover.result.ProverResultParser;

import com.google.common.collect.ImmutableList;

public class TrialProverStateMachine extends StateMachine {
	private MachineState initialState;
	private Prover prover;
	private ImmutableList<Role> roles;

	public TrialProverStateMachine() {

	}

	@Override
	public void initialize(List<Gdl> description) {
		roles = ImmutableList.copyOf(Role.computeRoles(description));
		initialState = computeInitialState();

	}

	private MachineState computeInitialState() {
		Set<GdlSentence> results = prover.askAll(ProverQueryBuilder.getInitQuery(), new HashSet<GdlSentence>());
		return new ProverResultParser().toState(results);
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
