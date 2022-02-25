package edu.isistan.pumas.framework.protocols.monotonicConcession.multilateralConcessionStrategy;

import java.util.List;

import edu.isistan.christian.recommenders.sur.datatypes.SURItem;
import edu.isistan.pumas.framework.protocols.commons.proposal.AgProposal;
import edu.isistan.pumas.framework.protocols.commons.userAgents.UserAg;

public class MultilateralConcessionStrategyUtilitarian <T extends SURItem> extends MultilateralConcessionStrategySocialBased<T>{
	
	public double getOtherAgentsUtility (AgProposal<T> p, List<UserAg<T>> otherAgents){
		double utility = 0;
		
		for (UserAg<T> ag : otherAgents){
			utility += ag.getUtilityFor(p);
		}
		
		return utility;
	}
	
	protected boolean isBetter (double utility1, double utility2){
		return utility1 > utility2;
	}

	@Override
	public String toString() {
		return "MultilateralConcessionStrategyUtilitarian []";
	}


}
