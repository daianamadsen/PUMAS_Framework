package edu.isistan.pumas.framework.protocols.monotonicConcession.multilateralConcessionStrategy;

import java.util.List;

import edu.isistan.christian.recommenders.sur.datatypes.SURItem;
import edu.isistan.pumas.framework.protocols.commons.proposal.AgProposal;
import edu.isistan.pumas.framework.protocols.commons.userAgents.UserAg;

public class MultilateralConcessionStrategyNash <T extends SURItem> extends
		MultilateralConcessionStrategySocialBased<T> {

	@Override
	public double getOtherAgentsUtility (AgProposal<T> p, List<UserAg<T>> otherAgents){		
		if (otherAgents.isEmpty())
			return 0;
		
		double utility = 1; //if we put 0 here the first product you do will be 0*utility = 0
		for (UserAg<T> ag : otherAgents){
			utility *= ag.getUtilityFor(p);
		}
		
		return utility;
	}
	
	protected boolean isBetter (double utility1, double utility2){
		/* Utility increment of the new proposal against the current proposal of the agent. 
		 * Ex: given a currentProposal = B, with getOtherAgentsUtility(currentProposal) = 5 and
		 * 	   given a p = C, with getOtherAgentsUtility(p) = 6
		 * Then if the next proposal to mak is p => the utility for the other agents will increase in 6-5=1.
		 * We look for the first proposal has better utility for the rest of the agents.
		 */
		
		return utility1 > utility2;
	}

	@Override
	public String toString() {
		return "MultilateralConcessionStrategyNash []";
	}
	
	

}
