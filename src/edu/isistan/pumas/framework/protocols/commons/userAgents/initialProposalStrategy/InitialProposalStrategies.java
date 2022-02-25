package edu.isistan.pumas.framework.protocols.commons.userAgents.initialProposalStrategy;

import edu.isistan.christian.recommenders.sur.datatypes.SURItem;

public enum InitialProposalStrategies {
	EGOCENTRIC ("Egocentric (Initial Proposal Strategy)");
	
	//-------------- For every element in the enum
	private String name;
	
	InitialProposalStrategies(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}
	
	public InitialProposalStrategy<SURItem> get(){
		switch(this){
		case EGOCENTRIC:
			return new InitialProposalStrategyEgocentric<>();
		default:
			return null;
		}
	}
	
	//For testing
	public static void main (String [] args){
		System.out.println("Testing: "+InitialProposalStrategies.valueOf("EGOCENTRIC").getName());
	}
	
}
