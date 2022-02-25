package edu.isistan.pumas.framework.protocols.commons.proposal;

import edu.isistan.christian.recommenders.sur.datatypes.SURItem;

/**
 * This kind of proposal is used when the agent has nothing more to propose.
 * Ex:  while making the initial proposal finds out that he has nothing to propose, 
 * 		while having to make a concession he decides that he can't concede anymore
 * @author Christian
 *
 */
public class EmptyAgProposal<T extends SURItem> extends AgProposal<T> {

	public EmptyAgProposal(String agentID) {
		super(agentID, null, 0.0);
	}

	@Override
	public boolean isEmpty() {
		return true;
	}
	
	@Override
	public boolean canBeConceded(){
		return false;
	}
	
	@Override
	public String toString() {
		return "AgProposal [ID=" + ID + ", agentID=" + agentID
				+", isEmpty= "+isEmpty()
				+", utilityValue="	+ utilityValue
				+"]";
	}
	
}
