package edu.isistan.pumas.framework.protocols.monotonicConcession.multilateralConcessionStrategy;

import edu.isistan.christian.recommenders.sur.datatypes.SURItem;

public enum MultilateralConcessionStrategies {
	LINEAL ("Lineal (Multilateral Strategy)"),
	NASH ("Nash (Multilateral Strategy)"),
	UTILITARIAN ("Utilitarian (Multilateral Strategy)"),
	DESIRESDISTANCE ("Desires Distance (Multilateral Strategy)"),
	DESIRESDISTANCE_BEST ("Desires Distance (Best) (Multilateral Strategy)"),
	EGOCENTRIC ("Egocentric (Multilateral Strategy)");


	//-------------- For every element in the enum
	private String name;

	MultilateralConcessionStrategies(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public MultilateralConcessionStrategy<SURItem> get(){
		MultilateralConcessionStrategy<SURItem> st = null;

		switch (this){
		case LINEAL:
			st = new MultilateralConcessionStrategyLineal<>(); break;
		case NASH:
			st = new MultilateralConcessionStrategyNash<>(); break;
		case UTILITARIAN:
			st = new MultilateralConcessionStrategyUtilitarian<>(); break;
		case DESIRESDISTANCE:
			st = new MultilateralConcessionStrategyDesiresDistance<>(); break;
		case DESIRESDISTANCE_BEST:
			st = new MultilateralConcessionStrategyDesiresDistanceBest<>(); break;
		case EGOCENTRIC:
			st = new MultilateralConcessionStrategyEgocentric<>(); break;
		default:
			break;
		}

		return st;
	}
	
	//For testing
	public static void main (String [] args){
		System.out.println("Testing: "+MultilateralConcessionStrategies.valueOf("DESIRESDISTANCE").getName());
	}
}
