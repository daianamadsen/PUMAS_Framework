package edu.isistan.pumas.framework.protocols.commons.userAgents.proposalAcceptanceStrategy;

import edu.isistan.christian.recommenders.sur.datatypes.SURItem;
import edu.isistan.pumas.framework.protocols.commons.userAgents.proposalAcceptanceStrategy.ProposalAcceptanceStrategyRelaxedS.RelaxLevel;

public enum ProposalAcceptanceStrategies {
	STRICT ("Strict (Proposal Acceptance Strategy)"),
	RELAXED ("Relaxed (Proposal Acceptance Strategy)"),
	RELAXED_S ("Relaxed_S (Proposal Acceptance Strategy)"),
	NEXT ("Next (Proposal Acceptance Strategy)");
	
	//-------------- For every element in the enum
	private String name;
	
	ProposalAcceptanceStrategies(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}
	
	public ProposalAcceptanceStrategy<SURItem> get(){
		switch(this){
		case STRICT:
			return new ProposalAcceptanceStrategyStrict<>();
		case RELAXED:
			return new ProposalAcceptanceStrategyRelaxed<>();
		case RELAXED_S:
			return new ProposalAcceptanceStrategyRelaxedS<>();
		case NEXT:
			return new ProposalAcceptanceStrategyNext<>();
		default:
			return null;
		}
	}
	
	//For testing
	public static void main (String [] args){
		System.out.println("Testing: "+ProposalAcceptanceStrategies.valueOf("STRICT").getName());
		System.out.println("Testing: "+ProposalAcceptanceStrategies.valueOf("RELAXED").getName());
		System.out.println("Testing: "+ProposalAcceptanceStrategies.valueOf("NEXT").getName());
		
		ProposalAcceptanceStrategyRelaxedS<SURItem> p = (ProposalAcceptanceStrategyRelaxedS<SURItem>) ProposalAcceptanceStrategies.RELAXED.get();
		p.setRelaxationLevel(RelaxLevel.HIGH);
		System.out.println("Testing: "+ p.toString());
	}
}
