package edu.isistan.pumas.framework.protocols.commons.userAgents.proposalAcceptanceStrategy;

import edu.isistan.christian.recommenders.sur.datatypes.SURItem;
import edu.isistan.pumas.framework.protocols.commons.proposal.AgProposal;
import edu.isistan.pumas.framework.protocols.commons.userAgents.UserAg;

public interface ProposalAcceptanceStrategy <T extends SURItem>{
	
	public final double PROPOSAL_ACCEPTANCE_PF_BETA = 0.25;
	public final double PROPOSAL_ACCEPTANCE_PF_GAMMA = 0.25;
	public final double PROPOSAL_ACCEPTANCE_PF_DELTA = 0.25;

	public boolean accepts (UserAg<T> ag, AgProposal<T> p);
	
	public String toString();
}
