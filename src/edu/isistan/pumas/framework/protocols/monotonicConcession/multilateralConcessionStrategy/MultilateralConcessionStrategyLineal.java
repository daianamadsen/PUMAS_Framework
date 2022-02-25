package edu.isistan.pumas.framework.protocols.monotonicConcession.multilateralConcessionStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.isistan.christian.recommenders.sur.datatypes.SURItem;
import edu.isistan.pumas.framework.protocols.commons.exceptions.NoProposalsAvailableException;
import edu.isistan.pumas.framework.protocols.commons.proposal.AgProposal;
import edu.isistan.pumas.framework.protocols.commons.proposal.comparators.ProposalUtilityComparator;
import edu.isistan.pumas.framework.protocols.commons.userAgents.UserAg;

public class MultilateralConcessionStrategyLineal<T extends SURItem> extends MultilateralConcessionStrategy<T> {

	protected AgProposal<T> getBestUtilityValueItem(List<AgProposal<T>> candidateProposals) throws NoProposalsAvailableException{
		
		if (candidateProposals.isEmpty())
			throw new NoProposalsAvailableException();
		
		List<AgProposal<T>> ordered = new ArrayList<>(candidateProposals);
		Collections.sort(ordered, new ProposalUtilityComparator<T>(false)); //order the items in descending order
		
		return ordered.get(0); //the first item is the one with greatest utility value
	}
	
	
	/**
	 * @param candidateProposals
	 * @return always the best item for the agent that called the method. 
	 * This strategy doesn't care the interests of the other agents)
	 * @throws NoProposalsAvailableException if "candidateProposals" is empty
	 */
	public AgProposal<T> getNextItemToPropose(UserAg<T> agent, 
			List<AgProposal<T>> candidateProposals) throws NoProposalsAvailableException {
		
		return this.getBestUtilityValueItem(candidateProposals); 
	}


	@Override
	public String toString() {
		return "MultilateralConcessionStrategyLineal []";
	}

	
}
