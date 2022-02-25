package edu.isistan.pumas.framework.protocols.commons.userAgents.proposalAcceptanceStrategy;

import edu.isistan.christian.recommenders.sur.datatypes.SURItem;
import edu.isistan.pumas.framework.protocols.commons.exceptions.NothingToProposeException;
import edu.isistan.pumas.framework.protocols.commons.proposal.AgProposal;
import edu.isistan.pumas.framework.protocols.commons.userAgents.UserAg;

public class ProposalAcceptanceStrategyRelaxedS<T extends SURItem> implements ProposalAcceptanceStrategy<T> {

	public enum RelaxLevel{
		LOW (0.025),
		MEDIUM (0.05),
		HIGH (0.1);
		
		private double utilitySacrificed;
		RelaxLevel (double utilitySacrificed){
			this.utilitySacrificed = utilitySacrificed;
		}
		
		private double getUtilitySacrificed(){
			return this.utilitySacrificed;
		}
		
		public double getRelaxLevelValue(){
			switch(this){
			case HIGH:
				return RelaxLevel.HIGH.getUtilitySacrificed();
			case LOW:
				return RelaxLevel.LOW.getUtilitySacrificed();
			case MEDIUM:
				return RelaxLevel.MEDIUM.getUtilitySacrificed();
			default:
				return 0.0;
			}
		}
	}

	
	RelaxLevel relaxationLevel = RelaxLevel.LOW; //DEFAULT
	/**
	 * 
	 * @param level If not set, defaults at RelaxLevel.LOW
	 */
	public ProposalAcceptanceStrategyRelaxedS (RelaxLevel level){
		relaxationLevel = level;
	}
	
	public ProposalAcceptanceStrategyRelaxedS(){
	}
	
	public RelaxLevel getRelaxationLevel() {
		return relaxationLevel;
	}

	public void setRelaxationLevel(RelaxLevel relaxationLevel) {
		this.relaxationLevel = relaxationLevel;
	}

	@Override
	public boolean accepts(UserAg<T> ag, AgProposal<T> p) {
		try {
			return (ag.getUtilityFor(p) >= ag.getUtilityFor(ag.getCurrentProposal()) - this.relaxationLevel.getRelaxLevelValue());
		} catch (NothingToProposeException e) {
			//if i'm here => ag.getCurrentProposal() threw an exception => 
			//he will always agree to other agent's proposal as he doesn't have anything to propose
			return true;
		} 
	}

	@Override
	public String toString() {
		return "ProposalStrategyRelaxedS [relaxationLevel=" + relaxationLevel + "]";
	}
	
	

}