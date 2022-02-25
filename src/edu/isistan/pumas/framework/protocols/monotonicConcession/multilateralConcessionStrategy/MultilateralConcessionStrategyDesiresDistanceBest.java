package edu.isistan.pumas.framework.protocols.monotonicConcession.multilateralConcessionStrategy;

import java.util.ArrayList;
import java.util.List;

import edu.isistan.christian.recommenders.sur.datatypes.SURItem;
import edu.isistan.pumas.framework.protocols.commons.exceptions.NoProposalsAvailableException;
import edu.isistan.pumas.framework.protocols.commons.exceptions.NonConcedableCurrentProposalException;
import edu.isistan.pumas.framework.protocols.commons.exceptions.NothingToProposeException;
import edu.isistan.pumas.framework.protocols.commons.proposal.AgProposal;
import edu.isistan.pumas.framework.protocols.commons.userAgents.UserAg;

public class MultilateralConcessionStrategyDesiresDistanceBest <T extends SURItem> extends
		MultilateralConcessionStrategyDesiresDistance<T> {

	/**
	 * @throws NonConcedableCurrentProposalException 
	 * 
	 */
	public AgProposal<T> getNextItemToPropose(UserAg<T> agent, List<AgProposal<T>> candidateProposals, List<UserAg<T>> allAgents)
			throws NoProposalsAvailableException, NonConcedableCurrentProposalException {
		
		if (candidateProposals.isEmpty())
			throw new NoProposalsAvailableException();
			
		AgProposal<T> next = null;
		/* OtherAgentsUtility: the utility of the current proposal of "agent" from the other agents perspective. 
		 * Given that this strategy inherits from a MultilateralConcessionStrategyDesiresDistanceLEQ which is a 
		 * MultilateralConcessionStrategyDesiresDistance strategy type, the "other agents utility" will be the DD (desires distance)
		 * of a proposal (READ MORE IN: MultilateralConcessionStrategyDesiresDistance class)
		 */
		List<UserAg<T>> otherAgents = getOtherAgents (agent, allAgents);
		double bestProposalUtil; //the best we have at the moment is the current proposal, any new proposal should be better than the current one
		try {
			bestProposalUtil = getOtherAgentsUtility(agent.getCurrentProposal(), otherAgents);
		} catch (NothingToProposeException e) {
			//Has proposed nothing => can't change it (can't propose anything new)
			throw new NonConcedableCurrentProposalException(e);
		}  
		
		//TO AVOID HAVING AN INFINITE LOOP IN THE NEGOTIATION (SEE THE EXPLANATION IN: MultilateralConcessionStrategyDesiresDistance)
		List<AgProposal<T>> filtered = new ArrayList<>();
		for (AgProposal<T> p : candidateProposals){
			if (agent.getUtilityFor(p) <= bestProposalUtil)
				filtered.add(p);
		}
		
		for (int i=0; i<filtered.size(); i++){
			double dd = getOtherAgentsUtility(filtered.get(i), otherAgents);
			if (isBetter(dd, bestProposalUtil)){
				next = filtered.get(i);
				bestProposalUtil = dd;
			}
			
		}
		if (next != null)
			return next;
		else
			throw new NoProposalsAvailableException();
	}

	@Override
	public String toString() {
		return "MultilateralConcessionStrategyDesiresDistanceBest []";
	}
	
	
}
