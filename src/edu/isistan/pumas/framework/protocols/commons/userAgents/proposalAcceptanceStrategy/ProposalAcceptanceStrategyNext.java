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
				double af = (ag.getAssertivenessFactor() > 0.0) ? ag.getAssertivenessFactor() : 1.0; //1.0 if not initialized
				double cf = (ag.getCooperativenessFactor() > 0.0) ? ag.getCooperativenessFactor() : 1.0; //1.0 if not initialized
				double rf = (ag.getRelationshipsFactor().get(p.getAgentID()) != null) ? ag.getRelationshipsFactor().get(p.getAgentID()) : 1.0; //1.0 if not initialized
				return (ag.getUtilityFor(p)*cf*rf >= ag.getUtilityFor(nextProp)*af); //if p is better than my next proposal => accept
				
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
