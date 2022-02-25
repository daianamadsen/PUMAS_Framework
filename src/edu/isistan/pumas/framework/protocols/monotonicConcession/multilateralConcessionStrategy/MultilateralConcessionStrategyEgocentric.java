package edu.isistan.pumas.framework.protocols.monotonicConcession.multilateralConcessionStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.isistan.christian.recommenders.sur.datatypes.SURItem;
import edu.isistan.pumas.framework.protocols.commons.exceptions.NoProposalsAvailableException;
import edu.isistan.pumas.framework.protocols.commons.exceptions.NonConcedableCurrentProposalException;
import edu.isistan.pumas.framework.protocols.commons.exceptions.NothingToProposeException;
import edu.isistan.pumas.framework.protocols.commons.proposal.AgProposal;
import edu.isistan.pumas.framework.protocols.commons.proposal.comparators.ProposalUtilityComparator;
import edu.isistan.pumas.framework.protocols.commons.userAgents.UserAg;

public class MultilateralConcessionStrategyEgocentric<T extends SURItem> extends
		MultilateralConcessionStrategy<T> {

	@Override
	public AgProposal<T> getNextItemToPropose(UserAg<T> agent,List<AgProposal<T>> candidateProposals)
			throws NoProposalsAvailableException, NonConcedableCurrentProposalException {
		
		double currPropUtility;
		try {
			currPropUtility = agent.getCurrentProposal().getUtilityValue();
		} catch (NothingToProposeException e) {
			//Has proposed nothing => can't change it (can't propose anything new)
			throw new NonConcedableCurrentProposalException(e);
		}
		//We allow only candidates with utility value lower than the proposal the agent is currently helding:
		//	- because the egocentric concession works this way 
		//	- and because if the agent is able to re-propose things we can fall into an infinite loop
		/* => if we use "<" we will skip proposals that have the same utility value than the current one (which woudn't happen if we use "<=") 
				because if the agent recycles the proposals => we can fall into a infinite loop. PROBLEM: this avoid the loops but discards a lot of potential agreements!
		 */
		
		List<AgProposal<T>> filtered = new ArrayList<>();
		for (AgProposal<T> p : candidateProposals){
			if (agent.getUtilityFor(p) < currPropUtility)
				filtered.add(p);
		}
		
		if (filtered.isEmpty())
			throw new NoProposalsAvailableException();
			
		/* Attempt to minimize the utility loss by ordering the proposals in descending order of its utility value. Also this increases the amount of 
		 * potential concessions the agent can make (because if the agent is proposing something of utility 10 and the he concedes and proposes something
		 * of utility 1, then the next potential concession should have utility value lower than "1", which can significantly reduce the chance to reach an agreement
		 * if all the agents do the same.
		 */
		Collections.sort(filtered, new ProposalUtilityComparator<T>(false));
		
		//The agent propose the next item which represents a lower utility for him (the first in the list)
		return filtered.get(0);
		
	}

	@Override
	public String toString() {
		return "MultilateralConcessionStrategyEgocentric []";
	}
	
	

}
