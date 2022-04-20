package edu.isistan.pumas.framework.protocols.monotonicConcession.negotiationStrategy;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.isistan.christian.recommenders.sur.datatypes.SURItem;
import edu.isistan.pumas.framework.protocols.commons.userAgents.UserAg;
/**
 * Selects who has to do the next concession by selecting the agent with the lowest
 * Zi value (which can be computed in different ways, see {@link WillingnessToRiskConflictStrategy},
 * {@link ProductIncreasingStrategy}, {@link MinimalUtilityLossStrategy})
 */
public abstract class ZeuthenStrategy<T extends SURItem> extends NegotiationStrategy<T> {
	
	private static final Logger logger = LogManager.getLogger(ZeuthenStrategy.class);
		
	/**
	 * @param myAgent
	 * @return the willingness to risk conflict for "myAgent" = Zi (or Z-value) for "myAgent"
	 */
	protected abstract double getZiFor (UserAg<T> myAgent, List<UserAg<T>> allAgents);
	
	/**
	 * @param concedingAgents the list of candidate agents from which the method will select the agent that has to make the next 
	 * concession  (agents who can concede)
	 * @param allAgents the list of all the agents that take a part in the negotiation
	 * @return the agent/s that has to concede using the Zeuthen strategy: the agent with the lowest Z-value 
	 * (willingness to risk conflict) is the one that has to concede. It will return an empty list if the "agents" list is empty
	 */
	protected List<UserAg<T>> applySelectionStrategy(List<UserAg<T>> concedingAgents, List<UserAg<T>> allAgents) {
		List<UserAg<T>> shouldConcede = new ArrayList<>();
		if (concedingAgents.size() > 1){
			UserAg<T> agMin = null;
			double minZi = Double.POSITIVE_INFINITY;

			String zis = "";
			for (UserAg<T> ag : concedingAgents){
				double agZi = this.getZiFor(ag, allAgents);
				zis+= ag.getID()+"= "+agZi+", ";
				if (agZi <= minZi && agZi != Double.POSITIVE_INFINITY){ //agZi != Double.POSITIVE_INFINITY just in case, we should never need this check if the agents in "agents" list can concede. 
					if (agZi == minZi && agMin != null) //if A and B have the same Zi => store A in the list of agents who should concede, and now B is the agMin.
						shouldConcede.add(agMin);
					else //agZi < minZi
						shouldConcede.clear(); //clear the list because there is a new minimum
					minZi = agZi;
					agMin = ag;
				}
			}
			shouldConcede.add(agMin); //the "last" minimum should be added to the list. If there were other agents with the same Zi as the agMin then they were added to the shouldConcede list. 
			logger.info(">> Zi's: "+zis);
		}
		else if (concedingAgents.size() == 1)
			shouldConcede.add(concedingAgents.get(0));
		
		return shouldConcede;
	}

}
