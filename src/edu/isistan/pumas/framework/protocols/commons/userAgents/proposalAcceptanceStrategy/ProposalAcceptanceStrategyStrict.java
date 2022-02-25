package edu.isistan.pumas.framework.protocols.commons.userAgents.proposalAcceptanceStrategy;

import edu.isistan.christian.recommenders.sur.datatypes.SURItem;
import edu.isistan.pumas.framework.protocols.commons.exceptions.NothingToProposeException;
import edu.isistan.pumas.framework.protocols.commons.proposal.AgProposal;
import edu.isistan.pumas.framework.protocols.commons.userAgents.UserAg;

public class ProposalAcceptanceStrategyStrict<T extends SURItem> implements ProposalAcceptanceStrategy<T> {

	@Override
	public boolean accepts(UserAg<T> ag, AgProposal<T> p) {
		try {
			return (ag.getUtilityFor(p) >= ag.getUtilityFor(ag.getCurrentProposal()));
		} catch (NothingToProposeException e) {
			//if i'm here => ag.getCurrentProposal() threw an exception => 
			//he will always agree to other agent's proposal as he doesn't have anything to propose
			return true;
		} 
	}

	@Override
	public String toString() {
		return "ProposalAcceptanceStrategyStrict []";
	}
	

}
