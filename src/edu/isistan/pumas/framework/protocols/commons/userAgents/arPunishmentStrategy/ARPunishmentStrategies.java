package edu.isistan.pumas.framework.protocols.commons.userAgents.arPunishmentStrategy;

import edu.isistan.christian.recommenders.sur.datatypes.SURItem;

public enum ARPunishmentStrategies {
	EASYGOING ("EASY GOING (Already Rated Punishment Strategy)"),
	FLEXIBLE ("FLEXIBLE (Already Rated Punishment Strategy)"),
	FLEXIBLE_PLUS ("FLEXIBLE PLUS (Already Rated Punishment Strategy)"),
	MINIMUM_SATISFACTION ("MINIMUM SATISFACTION (Already Rated Punishment Strategy)"),
	TABOO ("TABOO (Already Rated Punishment Strategy)"),
	NONE ("NONE (Already Rated Punishment Strategy)");
	
	//-------------- For every element in the enum
	private String name;
	
	ARPunishmentStrategies(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}
	
	public AlreadyRatedPunishmentStrategy<SURItem> get(){
		switch(this){
		case EASYGOING:
			return new ARPStrategyEasyGoing<SURItem>();
		case FLEXIBLE:
			return new ARPStrategyFlexible<SURItem>();
		case FLEXIBLE_PLUS:
			return new ARPStrategyFlexiblePlus<SURItem>();
		case MINIMUM_SATISFACTION:
			return new ARPSMinimumSatisfaction<SURItem>();
		case TABOO:
			return new ARPStrategyTaboo<SURItem>();
		case NONE: default:
			return null;
		}
	}
	
	//For testing
	public static void main (String [] args){
		System.out.println("Testing: "+ARPunishmentStrategies.valueOf("FLEXIBLE").getName());
	}
}
