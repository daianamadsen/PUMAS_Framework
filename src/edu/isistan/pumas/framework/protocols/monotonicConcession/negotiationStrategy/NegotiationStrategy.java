package edu.isistan.pumas.framework.protocols.monotonicConcession.negotiationStrategy;

import java.util.ArrayList;
import java.util.List;

import edu.isistan.christian.recommenders.sur.datatypes.SURItem;
import edu.isistan.pumas.framework.protocols.commons.userAgents.UserAg;

public abstract class NegotiationStrategy<T extends SURItem> {
	
	/**
	 * 
	 * @param agents
	 * @return the list of agents who can make an effective concession
	 */
	protected List<UserAg<T>> getUserAgThatCanConcede (List<UserAg<T>> agents){
		List<UserAg<T>> canConcede = new ArrayList<>();
		
		for (UserAg<T> ag : agents)
			if (ag.canConcede())
				canConcede.add(ag);
		
		return canConcede;
	}

	
	protected abstract List<UserAg<T>> applySelectionStrategy(List<UserAg<T>> concedingAgents, List<UserAg<T>> allAgents);
	
	/**
	 * 
	 * @return the agent who has to make the next concession, or NULL if there are no agents who can make the concession.
	 */
	public List<UserAg<T>> selectWhoHasToConcede(List<UserAg<T>> agents){		
		/* Only from those agents who can concede => select the one with minimum Zi (Z-value). We have to filter the agent list  
		 * (using the "can concede" filter) because in the paper (where the author explains how to apply this negotiation 
		 * strategy to a multilateral negotiation) says that simply checking for which agent the Z-value is minimal is 
		 * not a sufficient sophisticated criterion for decidng who should make the next concession, and that a potential 
		 * way around this problem may be to select the agent with the minimal Z-value amongst those that are actually able 
		 * to make an effective concession. */
		
		return applySelectionStrategy(getUserAgThatCanConcede(agents), agents);
	}
	
	public abstract String toString();
}
