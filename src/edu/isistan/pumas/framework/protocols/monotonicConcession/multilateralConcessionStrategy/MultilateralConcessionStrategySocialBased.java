package edu.isistan.pumas.framework.protocols.monotonicConcession.multilateralConcessionStrategy;

import java.util.ArrayList;
import java.util.List;

import edu.isistan.christian.recommenders.sur.datatypes.SURItem;
import edu.isistan.pumas.framework.protocols.commons.exceptions.NoProposalsAvailableException;
import edu.isistan.pumas.framework.protocols.commons.exceptions.NonConcedableCurrentProposalException;
import edu.isistan.pumas.framework.protocols.commons.exceptions.NothingToProposeException;
import edu.isistan.pumas.framework.protocols.commons.proposal.AgProposal;
import edu.isistan.pumas.framework.protocols.commons.userAgents.UserAg;

public abstract class MultilateralConcessionStrategySocialBased<T extends SURItem> extends
		MultilateralConcessionStrategy<T> {

	protected abstract double getOtherAgentsUtility (AgProposal<T> p, List<UserAg<T>> otherAgents);
	
	protected abstract boolean isBetter (double utility1, double utility2);

	
	protected List<UserAg<T>> getOtherAgents(UserAg<T> agent, List<UserAg<T>> allAgents){
		List<UserAg<T>> other = new ArrayList<> (allAgents);
		other.remove(agent);
		
		return other;
	}
	
	public AgProposal<T> getNextItemToPropose(UserAg<T> agent, List<AgProposal<T>> candidateProposals)
			throws NoProposalsAvailableException, NonConcedableCurrentProposalException {
		
		if (candidateProposals.isEmpty())
			throw new NoProposalsAvailableException();
		
		AgProposal<T> next = null;
		double currPropUtil; //the utility of the current proposal of "agent" from the other agents perspective
		List<UserAg<T>> otherAgents = getOtherAgents (agent, this.allAgents);
		try {
			currPropUtil = getOtherAgentsUtility(agent.getCurrentProposal(), otherAgents); 
		} catch (NothingToProposeException e) {
			//Has proposed nothing => can't change it (can't propose anything new)
			throw new NonConcedableCurrentProposalException(e);
		} 
		boolean nextFound = false;
		
		for (int i=0; i<candidateProposals.size() && !nextFound; i++){

			if (isBetter(getOtherAgentsUtility(candidateProposals.get(i), otherAgents), currPropUtil)){
				nextFound = true;
				next = candidateProposals.get(i);
			}
			
		}
		if (next != null)
			return next;
		else
			throw new NoProposalsAvailableException();
	}

}
