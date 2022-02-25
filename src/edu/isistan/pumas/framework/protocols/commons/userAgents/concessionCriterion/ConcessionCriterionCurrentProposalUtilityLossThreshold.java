package edu.isistan.pumas.framework.protocols.commons.userAgents.concessionCriterion;

import edu.isistan.christian.recommenders.sur.datatypes.SURItem;
import edu.isistan.pumas.framework.protocols.commons.exceptions.NothingToProposeException;
import edu.isistan.pumas.framework.protocols.commons.proposal.AgProposal;
import edu.isistan.pumas.framework.protocols.commons.userAgents.UserAg;

public class ConcessionCriterionCurrentProposalUtilityLossThreshold <T extends SURItem> implements
		ConcessionCriterion<T> {

	double utilityThreshold = 0.0;

	public ConcessionCriterionCurrentProposalUtilityLossThreshold() {
		super();
	}
	
	public ConcessionCriterionCurrentProposalUtilityLossThreshold(double utilityThreshold) {
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
		try {
			return !(agent.getUtilityFor(agent.getOriginalProposal())-agent.getUtilityFor(proposal) >= utilityThreshold);
		} catch (NothingToProposeException e) {
			return false;
		}
	}
	
	/**
	 * @param agent
	 * @return true if the loss of utility, considering the last proposal made by the agent (it's current proposal) 
	 * and the first proposal he made, is above (>) a certain threshold.
	 * Be aware of this
	 * - If utility(originalProposal) > utility(currentProp) = N, N will be positive. 
	 * - If utility(originalProposal) > utility(currentProp) = N, N will be negative. Then, if threshold is positive => will always concede, if negative will depend on the comparation between utilities.
	 */
	@Override
	public boolean canConcede(UserAg<T> agent) {
		
		try {
			return isAcceptableProposal(agent, agent.getCurrentProposal());
		} catch (NothingToProposeException e) {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return "ConcessionCriterionCurrentProposalUtilityLossThreshold [utilityThreshold="
				+ utilityThreshold + "]";
	}
	
	

}
