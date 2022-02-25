package edu.isistan.pumas.framework.protocols.commons.proposal;

import edu.isistan.christian.recommenders.sur.datatypes.SURItem;

public class ConflictDealAgProposal<T extends SURItem> extends AgProposal<T> {

	public ConflictDealAgProposal() {
		super("", null, 0.0);
	}

	@Override
	public boolean isConflictDeal() {
		return true;
	}
	
	@Override
	public boolean canBeConceded(){
		return false;
	}
	
	@Override
	public String toString() {
		return "AgProposal [ID=" + ID + ", conflictDeal= true]";
	}
}
