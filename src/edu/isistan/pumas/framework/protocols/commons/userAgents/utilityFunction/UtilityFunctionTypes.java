package edu.isistan.pumas.framework.protocols.commons.userAgents.utilityFunction;

import edu.isistan.christian.recommenders.sur.datatypes.SURItem;
import edu.isistan.pumas.framework.protocols.commons.userAgents.arPunishmentStrategy.ARPunishmentStrategies;

public enum UtilityFunctionTypes {
	RECOMMENDER_BASED ("RECOMMENDER BASED (Utility Function)");
	
	//-------------- For every element in the enum
	private String name;
	
	UtilityFunctionTypes(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}
	
	public UtilityFunction<SURItem> get(){
		switch(this){
		case RECOMMENDER_BASED:
			return new UtilityFunctionRecommenderBased<SURItem>();
		default:
			return null;
		}
	}
	
	//For testing
	public static void main (String [] args){
		System.out.println("Testing: "+ARPunishmentStrategies.valueOf("FLEXIBLE").getName());
	}
}
