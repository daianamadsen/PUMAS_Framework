package edu.isistan.pumas.framework.protocols.monotonicConcession.negotiationStrategy;

import java.util.ArrayList;
import java.util.List;

import edu.isistan.christian.recommenders.sur.datatypes.SURItem;
import edu.isistan.pumas.framework.protocols.commons.exceptions.NothingToProposeException;
import edu.isistan.pumas.framework.protocols.commons.proposal.AgProposal;
import edu.isistan.pumas.framework.protocols.commons.userAgents.UserAg;

public class ProductIncreasingStrategy<T extends SURItem> extends ZeuthenStrategy<T> {

	/**
	 * We compute the Zi value for the agent "myAgent" considering all the agent in the UserAgContainer, not only
	 * the agents that can concede. Otherwise the search for the minimum utility value between the proposals of the other
	 * agents will not consider the proposal of the agents that can't concede.
	 * @param myAgent
	 * @param allAgents
	 * @return the willingness to risk conflict for "myAgent" = Zi (or Z-value) for "myAgent"
	 */
	@Override
	protected double getZiFor (UserAg<T> myAgent, List<UserAg<T>> allAgents){
		List<UserAg<T>> otherAgents = new ArrayList<>(allAgents); //all the agents not only the ones that can concede???
		otherAgents.remove(myAgent);
		
		AgProposal<T> myAgentCurrProp;
		try {
			myAgentCurrProp = myAgent.getCurrentProposal();
		} catch (NothingToProposeException e) {
			/* Has proposed nothing => can't change it (can't propose anything new)
		     * this way the agent should never be able to concede using this strategy, but we shouldn't have been able 
		     * to use this strategy on an agent that can't concede in the first place so this is just for returning 
		     * proper values, will never be used */
			return Double.POSITIVE_INFINITY;
		}
		
		double agZi = myAgentCurrProp.getUtilityValue();
		
		for (UserAg<T> otherAg : otherAgents) //what the others think about my proposal
			agZi *= otherAg.getUtilityFor(myAgentCurrProp);
		
		return agZi;
	}

	@Override
	public String toString() {
		return "ProductIncreasingStrategy []";
	}
	
}
