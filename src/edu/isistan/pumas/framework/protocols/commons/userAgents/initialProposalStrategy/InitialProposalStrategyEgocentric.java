package edu.isistan.pumas.framework.protocols.commons.userAgents.initialProposalStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.isistan.christian.recommenders.sur.datatypes.SURItem;
import edu.isistan.pumas.framework.protocols.commons.exceptions.NoProposalsAvailableException;
import edu.isistan.pumas.framework.protocols.commons.proposal.AgProposal;
import edu.isistan.pumas.framework.protocols.commons.proposal.comparators.ProposalUtilityComparator;

public class InitialProposalStrategyEgocentric<T extends SURItem> implements InitialProposalStrategy<T> {
	
	/**
	 * 
	 * @param candidateProposals
	 * @return the proposal with the higher utility for its creator
	 * @throws NoProposalsAvailableException
	 */
	public AgProposal<T> makeInitialProposal (List<AgProposal<T>> candidateProposals) throws NoProposalsAvailableException{

		if (candidateProposals.isEmpty())
			throw new NoProposalsAvailableException();
		
		List<AgProposal<T>> ordered = new ArrayList<>(candidateProposals);
		Collections.sort(ordered, new ProposalUtilityComparator<T>(false)); //order the items in descending order
		
		return ordered.get(0); //the first item is the one with greatest utility value (the best item for the agent)
	}

	@Override
	public String toString() {
		return "InitialProposalStrategyEgocentric []";
	}
	
	
}
