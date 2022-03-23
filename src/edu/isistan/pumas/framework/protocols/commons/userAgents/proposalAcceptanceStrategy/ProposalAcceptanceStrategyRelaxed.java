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

	@Override
	public boolean accepts(UserAg<T> ag, AgProposal<T> p) {
		try {
			double af = (ag.getAssertivenessFactor() > 0.0) ? ag.getAssertivenessFactor() : 1.0; //1.0 if not initialized
			double cf = (ag.getCooperativenessFactor() > 0.0) ? ag.getCooperativenessFactor() : 1.0; //1.0 if not initialized
			return (ag.getUtilityFor(p)*cf >= ag.getUtilityFor(ag.getCurrentProposal())*af*(1 - this.relaxPercentage));
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
