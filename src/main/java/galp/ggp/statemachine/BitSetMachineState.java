package galp.ggp.statemachine;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

import org.ggp.base.util.gdl.grammar.GdlSentence;
import org.ggp.base.util.statemachine.MachineState;

import is.ru.cadia.ggp.propnet.structure.PropNetStructure;

public class BitSetMachineState extends MachineState {
	BitSet state;
	PropNetStructure structure;

	public BitSetMachineState(BitSet state,PropNetStructure structure) {
		super();
		this.state = state;
		this.structure = structure;
	}

	@Override
	public Set<GdlSentence> getContents() {
		Set<GdlSentence> contents = new HashSet<GdlSentence>();

		return contents;
	}

	@Override
	public MachineState clone() {
		return new BitSetMachineState(state,structure);
	}

}
