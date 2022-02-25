package edu.isistan.pumas.framework.protocols.commons.userAgents.proposalAcceptanceStrategy;

import edu.isistan.christian.recommenders.sur.datatypes.SURItem;
import edu.isistan.pumas.framework.protocols.commons.exceptions.NonConcedableCurrentProposalException;
import edu.isistan.pumas.framework.protocols.commons.proposal.AgProposal;
import edu.isistan.pumas.framework.protocols.commons.userAgents.UserAg;

public class ProposalAcceptanceStrategyNext <T extends SURItem> extends ProposalAcceptanceStrategyStrict<T>{

	@Override
	public boolean accepts(UserAg<T> ag, AgProposal<T> p) {
		if (super.accepts(ag, p)) 
			return true; //p is better than my current proposal
		else {
			try {
				AgProposal<T> nextProp = ag.peekNextProposal();
				return (ag.getUtilityFor(p) >= ag.getUtilityFor(nextProp)); //if p is better than my next proposal => accept
				
			} catch (NonConcedableCurrentProposalException e) {
				return false; //there is no next proposal (currentProposal can't be conceded) && p is not better than my current proposal (because of the first "if" clause)
			}
		}
			
	}

	@Override
	public String toString() {
		return "ProposalAcceptanceStrategyNext []";
	}
}
