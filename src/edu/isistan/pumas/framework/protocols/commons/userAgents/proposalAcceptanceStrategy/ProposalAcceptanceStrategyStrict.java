package edu.isistan.pumas.framework.protocols.commons.userAgents.proposalAcceptanceStrategy;

import edu.isistan.christian.recommenders.sur.datatypes.SURItem;
import edu.isistan.pumas.framework.protocols.commons.exceptions.NothingToProposeException;
import edu.isistan.pumas.framework.protocols.commons.proposal.AgProposal;
import edu.isistan.pumas.framework.protocols.commons.userAgents.UserAg;

public class ProposalAcceptanceStrategyStrict<T extends SURItem> implements ProposalAcceptanceStrategy<T> {

	private double pf_alpha;

	@Override
	public boolean accepts(UserAg<T> ag, AgProposal<T> p) {
		try {
			//Initialize Personality Factors
			pf_alpha = 1;
			double pf_beta = PROPOSAL_ACCEPTANCE_PF_BETA;
			double pf_gamma = PROPOSAL_ACCEPTANCE_PF_GAMMA;
			double pf_delta = PROPOSAL_ACCEPTANCE_PF_DELTA;
			double af = ag.getAssertivenessFactor();
			if (af >= 0.0) {
				pf_alpha = pf_alpha - pf_beta;
			} else {
				pf_beta = 0;
			}
			double cf = ag.getCooperativenessFactor();
			if (cf >= 0.0) {
				pf_alpha = pf_alpha - pf_gamma;
			} else {
				pf_gamma = 0;
			}
			double rf = 0;
			if (ag.getRelationshipsFactor().get(p.getAgentID()) != null) {
				pf_alpha = pf_alpha - pf_delta;
				rf = ag.getRelationshipsFactor().get(p.getAgentID());
			} else {
				pf_delta = 0;
			}
			double otherProposal = pf_alpha*ag.getUtilityFor(p) + pf_beta*(1-af) + pf_gamma*cf + pf_delta*rf;
			return (otherProposal >= ag.getUtilityFor(ag.getCurrentProposal()));
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
