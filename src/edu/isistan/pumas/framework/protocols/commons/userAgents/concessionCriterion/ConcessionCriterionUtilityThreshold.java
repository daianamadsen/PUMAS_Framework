package edu.isistan.pumas.framework.protocols.commons.userAgents.concessionCriterion;

import edu.isistan.christian.recommenders.sur.datatypes.SURItem;
import edu.isistan.pumas.framework.protocols.commons.exceptions.NonConcedableCurrentProposalException;
import edu.isistan.pumas.framework.protocols.commons.proposal.AgProposal;
import edu.isistan.pumas.framework.protocols.commons.userAgents.UserAg;

public class ConcessionCriterionUtilityThreshold<T extends SURItem> implements ConcessionCriterion<T>{

	double utilityThreshold = 0.0;

	public ConcessionCriterionUtilityThreshold() {
		super();
	}
	
	public ConcessionCriterionUtilityThreshold(double utilityThreshold) {
		super();
		this.utilityThreshold = utilityThreshold;
	}
	
	public double getUtilityThreshold() {
		return utilityThreshold;
	}

	public void setUtilityThreshold(double utilityThreshold) {
		this.utilityThreshold = utilityThreshold;
	}

	public boolean isAcceptableProposal(UserAg<T> agent, AgProposal<T> proposal){
		return (agent.getUtilityFor(proposal) >= this.utilityThreshold);
	}

	@Override
	public boolean canConcede(UserAg<T> agent) {
		AgProposal<T> nextItemToPropose = null;
		try {
			nextItemToPropose = agent.peekNextProposal();
			return isAcceptableProposal(agent, nextItemToPropose);
		} catch (NonConcedableCurrentProposalException e) {
			return false;
		}

	}

	@Override
	public String toString() {
		return "ConcessionCriterionUtilityThreshold [utilityThreshold="
				+ utilityThreshold + "]";
	}
	
}
