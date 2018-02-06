package galp.ggp.statemachine;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ggp.base.util.gdl.grammar.Gdl;
import org.ggp.base.util.gdl.grammar.GdlSentence;
import org.ggp.base.util.prover.Prover;
import org.ggp.base.util.prover.aima.AimaProver;
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
		List<Move> legals = null;
		prover = new AimaProver(description);
		roles = ImmutableList.copyOf(Role.computeRoles(description));
		initialState = computeInitialState();
		try {
			 legals =	getLegalMoves(initialState,roles.get(0));
		} catch (MoveDefinitionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (Move s : legals) {
			System.out.println(s.toString());
		}
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
		return roles;
	}

	@Override
	public MachineState getInitialState() {
		return initialState;
	}

	@Override
	public List<Move> getLegalMoves(MachineState state, Role role) throws MoveDefinitionException {
		 Set<GdlSentence> results = prover.askAll(ProverQueryBuilder.getLegalQuery(role), ProverQueryBuilder.getContext(state));

	        if (results.isEmpty())
	        {
	            throw new MoveDefinitionException(state, role);
	        }

	        return new ProverResultParser().toMoves(results);
	}

	@Override
	public MachineState getNextState(MachineState state, List<Move> moves) throws TransitionDefinitionException {
		 Set<GdlSentence> results = prover.askAll(ProverQueryBuilder.getNextQuery(), ProverQueryBuilder.getContext(state, getRoles(), moves));

	        for (GdlSentence sentence : results)
	        {
	            if (!sentence.isGround())
	            {
	                throw new TransitionDefinitionException(state, moves);
	            }
	        }

	        return new ProverResultParser().toState(results);
	}

}
