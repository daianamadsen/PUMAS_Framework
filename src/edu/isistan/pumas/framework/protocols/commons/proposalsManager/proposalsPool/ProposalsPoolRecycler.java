package edu.isistan.pumas.framework.protocols.commons.proposalsManager.proposalsPool;

import edu.isistan.christian.recommenders.sur.datatypes.SURItem;
import edu.isistan.pumas.framework.protocols.commons.proposal.AgProposal;

public class ProposalsPoolRecycler <T extends SURItem> extends ProposalsPool<T> {
	
	public ProposalsPoolRecycler() {
		super();
	}
	
	@Override
	public boolean allowsProposalsRecycling(){
		return true;
	}

	/**
	 * THis kind of pool allows to re-use the proposals that were conceded in the past, so
	 * it re-adds the proposals that are not the current proposal anymore (the old-current)
	 * to the candidate proposals list
	 * @param oldCurrent
	 */
	@Override
	protected void processOldCurrent(AgProposal<T> oldCurrent) {
		this.add(oldCurrent);
	}


	@Override
	public void prepareForRefill() {
		/* DOES NOTHING, because as pool allows recycling => the proposals that currently are
		 * inside of the candidateProposals list will stay there when you refill. "Refilling the 
		 * pool" will only add new items (keeping the existing ones).
		 */
	}
	
	@Override
	public String toString() {
		return "ProposalsPoolRecycler [ firstProposal=" + firstProposal
				+ ", currentProposal=" + currentProposal
				+ ", #candidateProposals=" + candidateProposals.size()
				+ "]";
	}

}
