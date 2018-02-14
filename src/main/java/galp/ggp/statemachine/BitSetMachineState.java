package galp.ggp.statemachine;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

import org.ggp.base.util.gdl.grammar.GdlSentence;
import org.ggp.base.util.statemachine.MachineState;

import is.ru.cadia.ggp.propnet.structure.PropNetStructure;
import is.ru.cadia.ggp.propnet.structure.components.BaseProposition;

public class BitSetMachineState extends MachineState {
	public BitSet state;
	PropNetStructure structure;
	BitSet seen = new BitSet();
	public BitSetMachineState(BitSet state,PropNetStructure structure) {
		super();
		this.state = state;
		this.structure = structure;
	}

	@Override
	public Set<GdlSentence> getContents() {
		Set<GdlSentence> contents = new HashSet<GdlSentence>();
		int a = state.nextSetBit(0);
		for(BaseProposition bp:structure.getBasePropositions()) {
			if(state.get(bp.id)) {
				for(GdlSentence s:bp.sentences) {
					contents.add(s);
				}
			}
		}
		return contents;
	}

	@Override
	public MachineState clone() {
		return new BitSetMachineState((BitSet)state.clone(),structure);
	}

}
