package edu.isistan.pumas.framework.protocols.monotonicConcession.negotiationStrategy;

import java.util.ArrayList;
import java.util.List;

import edu.isistan.christian.recommenders.sur.datatypes.SURItem;
import edu.isistan.pumas.framework.protocols.commons.exceptions.NothingToProposeException;
import edu.isistan.pumas.framework.protocols.commons.proposal.AgProposal;
import edu.isistan.pumas.framework.protocols.commons.userAgents.UserAg;

public class WillingnessToRiskConflictStrategy<T extends SURItem> extends ZeuthenStrategy<T> {

	/**
	 * For every agent of "agents" which is not "myAgent" compute the utility of the proposal of that agent using the 
	 * perspective of "myAgent" (myAgent.getUtilityFor(...). Then return the lowest of those utility values
	 * @param myAgent
	 * @param otherAgents (a list of agents that excludes 
	 * @return the utility value (form "myAgent" perspective) of the proposal with the lowest (minimum) utility value.
	 */
	protected double selectMinUtilityProposal (UserAg<T> myAgent, List<UserAg<T>> otherAgents){
		double minUtility = Double.POSITIVE_INFINITY;
		
		for (UserAg<T> ag : otherAgents){
			try {
				AgProposal<T> otherAgProp = ag.getCurrentProposal();
				if (myAgent.getUtilityFor(otherAgProp) < minUtility){
					minUtility = myAgent.getUtilityFor(otherAgProp);
				}
			} catch (NothingToProposeException e) {
				//Ignore this agent // Do nothing if what he had proposed was NOTHING
			}
			
		}
		
		return minUtility;
	}
	
	/**
	 * We compute the Zi value for the agent "myAgent" considering all the agents in the UserAgContainer, not only
	 * the agents that can concede. Otherwise the search for the minimum utility value between the proposals of the other
	 * agents will not consider the proposal of the agents that can't concede.
	 * @param myAgent
	 * @param allAgents all the agents
	 * @return the willingness to risk conflict for "myAgent" = Zi (or Z-value) for "myAgent"
	 */
	protected double getZiFor (UserAg<T> myAgent, List<UserAg<T>> allAgents){
		//All the agents not only the ones that can concede, this is because of what I've explained in the method description (above)
		List<UserAg<T>> otherAgents = new ArrayList<>(allAgents); 
		otherAgents.remove(myAgent);
		
		double myUtility;
		try {
			myUtility = myAgent.getUtilityFor(myAgent.getCurrentProposal());
		} catch (NothingToProposeException e) {
			return Double.POSITIVE_INFINITY;
		}
		
		if (myUtility == 0)
			return 1;
		else			
			return ((myUtility - selectMinUtilityProposal(myAgent, otherAgents))/myUtility); //zi
	}

	@Override
	public String toString() {
		return "WillingnessToRiskConflictStrategy []";
	}
	
}
