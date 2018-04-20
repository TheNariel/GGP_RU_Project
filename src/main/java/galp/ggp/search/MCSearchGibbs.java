package galp.ggp.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.Role;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

import galp.ggp.statemachine.TimeOutException;

public class MCSearchGibbs {
	StateMachine propNetStateMachine;
	int nStates;
	List<HashMap<Move, Double[]>> GibbTables = new ArrayList<HashMap<Move, Double[]>>();

	public MCSearchGibbs(StateMachine propNetStateMachine) {
		this.propNetStateMachine = propNetStateMachine;
		for (int i = 0; i < propNetStateMachine.getRoles().size(); i++) {
			GibbTables.add(new HashMap<Move, Double[]>());
		}

	}

	public Move search(Node root, long timeout, Role role)
			throws MoveDefinitionException, TransitionDefinitionException, GoalDefinitionException {
		nStates = 0;
		try {
			mcSearch(root, timeout);
		} catch (TimeOutException e) {

			System.out.println("no more time, Get out, out, out .... ");
		}

		// finding the move with bigest N
		Move bestMove = null;
		for (int r = 0; r < propNetStateMachine.getRoles().size(); r++) {
			if (propNetStateMachine.getRoles().get(r).equals(role)) {
				int i = 0;
				int max = root.N[r][0];
				for (int m = 1; m < root.N[r].length; m++) {
					if (root.N[r][m] > max) {
						max = root.N[r][m];
						i = m;
					}
				}

				bestMove = root.legalActions.get(r).get(i);

			}
		}

		for (int r = 0; r < root.N.length; r++) {
			for (int m = 0; m < root.N[r].length; m++) {
				System.out.println(root.legalActions.get(r).get(m) + "|Q: " + root.Q[r][m] + " |N: " + root.N[r][m]);
			}

		}
		printTable();
		System.out.println(bestMove + " n of simulations: " + nStates);
		System.out.println();
		// returning the move.
		return bestMove;
	}

	// this method make a new tree node from game state.
	// Q and N are stored in two dimensional arrays where first index is the role
	// the belong to and second is the action. they have the same size/dimension as
	// the legalAction list of lists
	// where are all the legal moves for all the roles.
	// next is hash map, maping the string representation of joint move to a Node,
	// representing state after that move.
	public Node initNextNode(MachineState state, Node parent, List<Integer> moveFromParent)
			throws MoveDefinitionException {

		List<List<Move>> legalActions = new ArrayList<List<Move>>();
		for (Role role : propNetStateMachine.getRoles()) {
			legalActions.add(propNetStateMachine.getLegalMoves(state, role));
		}
		// System.out.println(legalActions);
		double[][] Q = new double[legalActions.size()][];
		int[][] N = new int[legalActions.size()][];
		int i = 0;
		for (List<Move> m : legalActions) {
			Q[i] = new double[m.size()];
			N[i] = new int[m.size()];
			i++;
		}
		Hashtable<String, Node> next = new Hashtable<String, Node>();

		return new Node(state, parent, legalActions, next, Q, N, moveFromParent);
	}

	// Main method of the search, going throw all four stages.
	public void mcSearch(Node root, long timeout)
			throws MoveDefinitionException, TransitionDefinitionException, GoalDefinitionException, TimeOutException {
		List<Integer> indexes;
		List<Move> moves;

		while (System.currentTimeMillis() + 100 <= timeout) {
			nStates++;
			Node node = root;
			// selection selection(node) chooses the best actions for all players and if we
			// have corresponding state in tree its repeated there.
			while (true) {
				moves = new ArrayList<Move>();
				indexes = selection(node);
				// contents = "[";
				for (int i = 0; i < indexes.size(); i++) {
					Move m = node.legalActions.get(i).get(indexes.get(i));
					// contents += m.toString() + ", ";
					moves.add(m);
				}
				// contents = contents.substring(0, contents.length() - 2);
				// contents += "]";
				if (node.exploredChildren.containsKey(indexes.toString())) {
					node = node.exploredChildren.get(indexes.toString());
					if (node.terminal)
						break;
				} else {
					break;
				}
			}
			if (node.terminal) {

				backProp(node, node.values);
			} else {

				// expand
				MachineState nextstate = propNetStateMachine.getNextState(node.state, moves);
				Node next = initNextNode(nextstate, node, indexes);
				if (propNetStateMachine.isTerminal(next.state)) {
					next.terminal = true;
					List<Integer> ret = new ArrayList<Integer>();
					for (Role r : propNetStateMachine.getRoles()) {
						ret.add(propNetStateMachine.getGoal(next.state, r));
					}
					next.values = ret;

					backProp(next, next.values);
					node.exploredChildren.put(indexes.toString(), next);
				} else {
					node.exploredChildren.put(indexes.toString(), next);

					// playout
					List<Integer> value;

					value = runSimulation(next.state, timeout);

					// backprop
					backProp(next, value);

				}
			}
		}

	}

	// the UCT selection method.
	public List<Integer> selection(Node node) {
		int C = 50;
		Random rand = new Random();
		List<List<Double>> A = new ArrayList<List<Double>>();

		List<Integer> indexes = new ArrayList<Integer>();
		// getting the A values for all the actions for all the roles.
		for (int r = 0; r < node.N.length; r++) {
			List<Double> mo = new ArrayList<Double>();
			for (int m = 0; m < node.N[r].length; m++) {
				if (node.N[r][m] == 0) {
					mo.add((double) Integer.MAX_VALUE);
				} else {
					double h = node.Q[r][m];
					double huu = node.N[r][m];
					double huuu = node.getNodeN();
					double hu = Math.log(huuu) / huu;
					double hh = Math.sqrt(hu);

					mo.add(h + C * hh);
				}
			}
			A.add(mo);
		}

		// getting index of the biggest A. if there is multiple of them than pick random
		// among the bigest.
		for (int r = 0; r < A.size(); r++) {
			double max = 0;
			List<Integer> mo = new ArrayList<Integer>();
			for (int m = 0; m < A.get(r).size(); m++) {
				if (A.get(r).get(m) > max) {
					mo.clear();
					mo.add(m);
					max = A.get(r).get(m);
				} else {
					if (A.get(r).get(m) == max) {
						mo.add(m);
					}
				}
			}

			indexes.add(mo.get(rand.nextInt(mo.size())));
		}

		return indexes;
	}

	// back probation starting with newly expanded node and updating parent until
	// there are no parents.
	public void backProp(Node node, List<Integer> value) {
		while (node.parent != null) {
			for (int i = 0; i < node.moveFromParent.size(); i++) {
				node.parent.Q[i][node.moveFromParent.get(i)] = node.parent.Q[i][node.moveFromParent.get(i)]
						+ ((value.get(i) - node.parent.Q[i][node.moveFromParent.get(i)])
								/ (node.parent.N[i][node.moveFromParent.get(i)] + 1));
				node.parent.N[i][node.moveFromParent.get(i)] += 1;
				addOrUpdate(node.parent.legalActions.get(i).get(node.moveFromParent.get(i)), value.get(i), i);
			}

			node = node.parent;
		}
	}

	// simulating the random playuot of the game, returning values for all the
	// roles.
	public List<Integer> runSimulation(MachineState state, long timeout)
			throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException, TimeOutException {

		while (!propNetStateMachine.isTerminal(state)) {
			if (System.currentTimeMillis() + 100 >= timeout)
				throw new TimeOutException();
			List<Move> moves = new ArrayList<>();
			List<Double> p = new ArrayList<>();

			Double[] value;
			double t = 1;

			int r = 0;
			List<List<Move>> actions = new ArrayList<>();
			for (Role role : propNetStateMachine.getRoles()) {
				actions.add(propNetStateMachine.getLegalMoves(state, role));
			}
			for (List<Move> M : actions) {
				if (System.currentTimeMillis() + 100 >= timeout)
					throw new TimeOutException();
				for (Move move : M) {
					addIfNew(move, r);
				}

				List<Double> temp = new ArrayList<>();

				r++;
			}
			Double[] Qb = getQb(t);
			r = 0;

			for (List<Move> M : actions) {
				p = new ArrayList<>();
				if (System.currentTimeMillis() + 100 >= timeout)
					throw new TimeOutException();
				for (Move move : M) {
					value = GibbTables.get(r).get(move);
					p.add(Math.pow(Math.E, (value[0] / t)) / Qb[r]);
				}

				Double max = Collections.max(p);
				moves.add(M.get(p.indexOf(max)));

				r++;
			}
			state = propNetStateMachine.getNextState(state, moves);
			// state = propNetStateMachine.getNextState(state,
			// propNetStateMachine.getRandomJointMove(state));
		}

		List<Integer> ret = new ArrayList<Integer>();
		for (Role r : propNetStateMachine.getRoles()) {
			ret.add(propNetStateMachine.getGoal(state, r));
		}
		return ret;

	}

	public Double[] getQb(double t) {
		Double[] ret = new Double[2], value;
		Double v;
		int i = 0;
		for (HashMap<Move, Double[]> Gibbtable : GibbTables) {
			v = 0.0;
			for (Move move : Gibbtable.keySet()) {
				value = Gibbtable.get(move);
				if (value[1] == 0) {
					v += Math.pow(Math.E, (100 / t));
				} else {
					v += Math.pow(Math.E, (value[0] / t));
				}
			}
			ret[i] = v;
			i++;
		}
		return ret;
	}

	private void addOrUpdate(Move move, double value, int role) {
		if (!GibbTables.get(role).containsKey(move)) {
			Double[] temp = new Double[] { value, 1.0 };

			GibbTables.get(role).put(move, temp);
		} else {
			Double[] temp = GibbTables.get(role).get(move);
			if (temp[1] == 0) {
				temp[0] = value;

			} else {
				temp[0] = (temp[1] * temp[0] + value) / (temp[1] + 1);

			}
			temp[1] += 1;
		}
	}

	private void addIfNew(Move move, int role) {
		if (!GibbTables.get(role).containsKey(move)) {
			Double[] temp = new Double[] { 100.0, 0.0 };
			GibbTables.get(role).put(move, temp);
		}
	}

	private void printTable() {
		for (HashMap<Move, Double[]> hashMap : GibbTables) {
			for (Move move : hashMap.keySet()) {
				System.out.print(move + "=[" + hashMap.get(move)[0] + "," + hashMap.get(move)[1] + "], ");
			}
			System.out.println("----------");
		}
	}
}
