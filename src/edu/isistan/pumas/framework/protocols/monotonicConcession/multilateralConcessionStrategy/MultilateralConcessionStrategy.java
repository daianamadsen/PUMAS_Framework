package edu.isistan.pumas.framework.protocols.monotonicConcession.multilateralConcessionStrategy;

import java.util.ArrayList;
import java.util.List;

import edu.isistan.christian.recommenders.sur.datatypes.SURItem;
import edu.isistan.pumas.framework.protocols.commons.exceptions.NoProposalsAvailableException;
import edu.isistan.pumas.framework.protocols.commons.exceptions.NonConcedableCurrentProposalException;
import edu.isistan.pumas.framework.protocols.commons.proposal.AgProposal;
import edu.isistan.pumas.framework.protocols.commons.userAgents.UserAg;

public abstract class MultilateralConcessionStrategy <T extends SURItem> {
	
	protected List<UserAg<T>> allAgents = new ArrayList<UserAg<T>>(); //in case is not set prior to the execution of the getNextgetNextItemToPropose method
	
	public void setAgentsInNegotiation (List<UserAg<T>> agents){
		this.allAgents = agents;
	}

	public abstract AgProposal<T> getNextItemToPropose(UserAg<T> agent, List<AgProposal<T>> candidateProposals) throws NoProposalsAvailableException, NonConcedableCurrentProposalException;
	
	public abstract String toString();
}
