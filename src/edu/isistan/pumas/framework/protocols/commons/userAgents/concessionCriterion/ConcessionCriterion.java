package edu.isistan.pumas.framework.protocols.commons.userAgents.concessionCriterion;

import edu.isistan.christian.recommenders.sur.datatypes.SURItem;
import edu.isistan.pumas.framework.protocols.commons.proposal.AgProposal;
import edu.isistan.pumas.framework.protocols.commons.userAgents.UserAg;

public interface ConcessionCriterion <T extends SURItem> {

	public boolean isAcceptableProposal (UserAg<T> agent, AgProposal<T> proposal);
	
	public boolean canConcede (UserAg<T> agent);

	public String toString();
}
