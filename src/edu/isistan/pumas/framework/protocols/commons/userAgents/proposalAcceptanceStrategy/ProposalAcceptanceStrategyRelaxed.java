package edu.isistan.pumas.framework.protocols.commons.userAgents.proposalAcceptanceStrategy;

import edu.isistan.christian.recommenders.sur.datatypes.SURItem;
import edu.isistan.pumas.framework.protocols.commons.exceptions.NothingToProposeException;
import edu.isistan.pumas.framework.protocols.commons.proposal.AgProposal;
import edu.isistan.pumas.framework.protocols.commons.userAgents.UserAg;

public class ProposalAcceptanceStrategyRelaxed<T extends SURItem> implements ProposalAcceptanceStrategy<T> {

//	public enum RelaxLevel{
//		LOW (0.025),
//		MEDIUM (0.05),
//		HIGH (0.1);
//		
//		private double utilitySacrificed;
//		RelaxLevel (double utilitySacrificed){
//			this.utilitySacrificed = utilitySacrificed;
//		}
//		
//		private double getUtilitySacrificed(){
//			return this.utilitySacrificed;
//		}
//		
//		public double getRelaxLevelValue(){
//			switch(this){
//			case HIGH:
//				return RelaxLevel.HIGH.getUtilitySacrificed();
//			case LOW:
//				return RelaxLevel.LOW.getUtilitySacrificed();
//			case MEDIUM:
//				return RelaxLevel.MEDIUM.getUtilitySacrificed();
//			default:
//				return 0.0;
//			}
//		}
//	}

	
	private double relaxPercentage = 0; //DEFAULT
	/**
	 * 
	 * @param level If not set, defaults at RelaxLevel.LOW
	 */
	public ProposalAcceptanceStrategyRelaxed (double relaxPercentage){
		this.relaxPercentage = relaxPercentage;
	}
	
	public ProposalAcceptanceStrategyRelaxed(){
	}
	
	public double getRelaxationLevel() {
		return relaxPercentage;
	}

	public void setRelaxationLevel(double relaxPercentage) {
		this.relaxPercentage = relaxPercentage;
	}

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
			return (otherProposal >= ag.getUtilityFor(ag.getCurrentProposal())*(1 - this.relaxPercentage));
		} catch (NothingToProposeException e) {
			//if i'm here => ag.getCurrentProposal() threw an exception => 
			//he will always agree to other agent's proposal as he doesn't have anything to propose
			return true;
		} 
	}

	@Override
	public String toString() {
		return "ProposalStrategyRelaxed [relaxationLevel=" + relaxPercentage + "]";
	}
	
	

}
