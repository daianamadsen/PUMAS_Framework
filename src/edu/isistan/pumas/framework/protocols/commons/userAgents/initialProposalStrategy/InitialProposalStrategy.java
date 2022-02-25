package edu.isistan.pumas.framework.protocols.commons.userAgents.initialProposalStrategy;

import java.util.List;

import edu.isistan.christian.recommenders.sur.datatypes.SURItem;
import edu.isistan.pumas.framework.protocols.commons.exceptions.NoProposalsAvailableException;
import edu.isistan.pumas.framework.protocols.commons.proposal.AgProposal;

public interface InitialProposalStrategy <T extends SURItem> {

	public AgProposal<T> makeInitialProposal (List<AgProposal<T>> candidateProposals) throws NoProposalsAvailableException;

	public String toString();
}
