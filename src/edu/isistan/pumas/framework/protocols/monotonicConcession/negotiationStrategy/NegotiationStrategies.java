package edu.isistan.pumas.framework.protocols.monotonicConcession.negotiationStrategy;

import edu.isistan.christian.recommenders.sur.datatypes.SURItem;

public enum NegotiationStrategies {
	WILLINGNESS_TO_RISK_CONFLICT ("Willingness to Risk Conflict (Negotiation Strategy)"), 
	PRODUCT_INCREASING ("Product Increasing (Negotiation Strategy)"), 
	MINIMAL_UTILITY_LOSS ("Minimal Utility Loss (Negotiation Strategy)");

	//-------------- For every element in the enum
		private String name;

		NegotiationStrategies(String name){
			this.name = name;
		}

		public String getName(){
			return name;
		}

	
	public NegotiationStrategy<SURItem> get (){
		NegotiationStrategy<SURItem> st = null;
		switch(this){
		case WILLINGNESS_TO_RISK_CONFLICT:
			st = new WillingnessToRiskConflictStrategy<>(); break;
		case PRODUCT_INCREASING:
			st = new ProductIncreasingStrategy<>(); break;
		case MINIMAL_UTILITY_LOSS:
			st = new MinimalUtilityLossStrategy<>(); break;
		default:
			break;
		}

		return st;
	}

	//For testing
	public static void main (String [] args){
		System.out.println("Testing: "+NegotiationStrategies.valueOf("WILLINGNESS_TO_RISK_CONFLICT").getName());
	}
}
