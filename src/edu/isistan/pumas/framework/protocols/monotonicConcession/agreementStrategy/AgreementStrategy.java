package edu.isistan.pumas.framework.protocols.monotonicConcession.agreementStrategy;

import java.util.List;

import edu.isistan.christian.recommenders.sur.datatypes.SURItem;
import edu.isistan.pumas.framework.protocols.commons.proposal.AgProposal;
import edu.isistan.pumas.framework.protocols.commons.userAgents.UserAg;

public interface AgreementStrategy<T extends SURItem>{

	/**
	 * 
	 * @param item
	 * @param agents
	 * @return true if all the agents agree with that item (they are happy enough with it and all of them accept the item)
	 */
	public boolean checkAgreement (AgProposal<T> proposal, List<UserAg<T>> agents);
	
	public String toString();
}
