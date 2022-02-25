package edu.isistan.pumas.framework.protocols.monotonicConcession.agreementStrategy;

import java.util.List;

import edu.isistan.christian.recommenders.sur.datatypes.SURItem;
import edu.isistan.pumas.framework.protocols.commons.proposal.AgProposal;
import edu.isistan.pumas.framework.protocols.commons.userAgents.UserAg;

public class AgreementStrategyMultilateral<T extends SURItem> implements AgreementStrategy<T> {

	@Override
	public boolean checkAgreement(AgProposal<T> proposal, List<UserAg<T>> agents) {
		int accepts = 0;
				
		for (int i=0; i<agents.size(); i++){
			if (agents.get(i).accepts(proposal))
				accepts++;
		}
		
		return (accepts == agents.size()); //true if all the agents accepted the proposal
	}

	/*
	 * This is the optimal way to check the agreement as it will stop asking to the agents when there is one that does not accept the proposal.
	 * The problem with this implementation is that if 1 agent is does not accept the proposal the rest of the agents don't get asked and therefore 
	 * their statistics (regarding proposals accepted/rejected) get biased.
	 */
	
//	@Override
//	public boolean checkAgreement(AgProposal<T> proposal, List<UserAg<T>> agents) {
//		boolean agreement = true;
//		
//		for (int i=0; i<agents.size() && agreement; i++){
//			agreement = agents.get(i).accepts(proposal);
//		}
//		
//		return agreement;
//	}
	

	@Override
	public String toString() {
		return "AgreementStrategyMultilateral []";
	}

}
