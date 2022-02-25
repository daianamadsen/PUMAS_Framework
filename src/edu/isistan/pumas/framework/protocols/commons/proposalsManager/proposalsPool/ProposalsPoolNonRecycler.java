package edu.isistan.pumas.framework.protocols.commons.proposalsManager.proposalsPool;

import java.util.ArrayList;
import java.util.List;

import edu.isistan.christian.recommenders.sur.datatypes.SURItem;
import edu.isistan.pumas.framework.protocols.commons.proposal.AgProposal;

public class ProposalsPoolNonRecycler<T extends SURItem> extends ProposalsPool<T> {
	
	//Historical Ranking 
	protected ArrayList<AgProposal<T>> historicalProposals; //List of concessions made by the agent in the current negotiation (proposals abandoned because he made a concession)
	//Discarded proposals (non-conceded and they were erased from the cache)
	protected ArrayList<AgProposal<T>> discardedProposals;

	public ProposalsPoolNonRecycler() {
		super();
		this.historicalProposals = new ArrayList<>();
		this.discardedProposals = new ArrayList<>();
	}
	
	@Override
	public void remove(AgProposal<T> p) {
		super.remove(p);
		//in case that it is in historicalProposals list
		historicalProposals.remove(p);
		//in case that it is in discardedProposals list
		discardedProposals.remove(p);
	}

	@Override
	public boolean allowsProposalsRecycling() {
		return false;
	}

	/**
	 * THis kind of pool DOES NOT allow to re-use the proposals that were conceded in the past, so
	 * the proposals that are not the current proposal anymore (the old-current) are stored in the
	 * historicalProposals list
	 * @param oldCurrent
	 */
	@Override
	protected void processOldCurrent(AgProposal<T> oldCurrent) {
		historicalProposals.add(oldCurrent);
	}
	
	/**
	 * When the pool is going to be refilled the proposals that are currently in the candidateProposals list
	 * are discarded, and therefore added to the discardedProposals list (to be used later when the method "resetPoolContents()" gets invoked) 
	 */
	@Override
	public void prepareForRefill() {
		discardedProposals.addAll(this.candidateProposals);
		this.candidateProposals.clear();
	}

	/**
	 * Additionally to the things done in the ProposalsPool we need to clear the other lists. 
	 */
	@Override
	public void reset(){
		super.reset();
		historicalProposals.clear();
		discardedProposals.clear();
	}

	/**
	 * 
	 * @return all the proposals in the pool. The proposals added depend on the type of the pool. 
	 * Initially it contains the current proposal and the candidateProposals. The firstProposal 
	 * is already inside of the candidateProposals list.  
	 */
	@Override
	public List<AgProposal<T>> getAllProposals() {
		ArrayList<AgProposal<T>> allProposals = new ArrayList<>();
		allProposals.add (currentProposal);
		allProposals.addAll(candidateProposals);
		allProposals.addAll(historicalProposals);
		allProposals.addAll(discardedProposals);
		
		return allProposals;
	}

	@Override
	public String toString() {
		return "ProposalsPoolNonRecycler [ firstProposal=" + firstProposal
				+ ", currentProposal=" + currentProposal 
				+ ", #candidateProposals=" + candidateProposals.size() 
				+ ", #historicalProposals=" + historicalProposals.size()
				+ ", #discardedProposals=" + discardedProposals.size() 
				+ "]";
	}

	
	
}
