package edu.isistan.pumas.framework.protocols.monotonicConcession.agreementStrategy;

import edu.isistan.christian.recommenders.sur.datatypes.SURItem;

public enum AgreementStrategies {
	MULTILATERAL ("Multilateral (Agreement Strategy)");

	//-------------- For every element in the enum
	private String name;
	AgreementStrategies(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public AgreementStrategy<SURItem> get(){
		switch (this){
		case MULTILATERAL:
			return new AgreementStrategyMultilateral<>();
		default: 
			return null;
		}
	}
	
	//For testing
	public static void main (String [] args){
		System.out.println("Testing: "+AgreementStrategies.valueOf("MULTILATERAL").getName());
	}
}
