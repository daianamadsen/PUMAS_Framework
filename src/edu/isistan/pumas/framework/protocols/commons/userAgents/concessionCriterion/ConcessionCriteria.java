package edu.isistan.pumas.framework.protocols.commons.userAgents.concessionCriterion;

import edu.isistan.christian.recommenders.sur.datatypes.SURItem;

public enum ConcessionCriteria {
	UTILITY_THRESHOLD ("Utility Threshold (Concession Criterion)"), 
	CURRENT_PROPOSAL_UTILITY_LOSS_THRESHOLD ("Current Proposal Utility Loss Threshold (Concession Criterion)");

	//-------------- For every element in the enum
	private String name;
	ConcessionCriteria(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public ConcessionCriterion<SURItem> get(){
		switch(this){
		case UTILITY_THRESHOLD:
			return new ConcessionCriterionUtilityThreshold<>();
		case CURRENT_PROPOSAL_UTILITY_LOSS_THRESHOLD:
			return new ConcessionCriterionCurrentProposalUtilityLossThreshold<>();
		default:
			return null;
		}
	}

	//For testing
	public static void main (String [] args){
		System.out.println("Testing: "+ConcessionCriteria.valueOf("UTILITY_THRESHOLD").getName());
	}

}
