package edu.isistan.pumas.framework.protocols.commons.userAgents.utilityFunction;

import java.util.ArrayList;
import java.util.List;

import edu.isistan.christian.recommenders.sur.datatypes.SURItem;
import edu.isistan.pumas.framework.dataTypes.NegotiableItem;
import edu.isistan.pumas.framework.protocols.commons.proposal.AgProposal;
import edu.isistan.pumas.framework.protocols.commons.userAgents.UserAg;

public abstract class UtilityFunction<T extends SURItem> {

	public double evaluate (UserAg<T> agent, AgProposal<T> proposal){
		if (proposal.isEmpty() || proposal.isConflictDeal()) //in this case the proposal.getItemProposed() returns null as there's no item in them
			return 0.0;

		double utility = 0.0;

		if (proposal.getAgentID().equals(agent.getID())){
			//if it was proposed by the agent which is making the evaluation => then the utilityValue
			// of the proposal is the value I should return
			utility = proposal.getUtilityValue();
		}
		else{
			utility = this.assessNegotiableItem(agent, proposal.getItemProposed());
		}

		return utility;
	}

	public double assessNegotiableItem (UserAg<T> agent, NegotiableItem<T> nItem){
		List<T> items = new ArrayList<>(nItem.getItems());
		if (items.isEmpty()) 
			return 0.0;
		if (items.size() == 1)
			return evaluateItem(agent, items.get(0));
		else{
			//Evaluate every item and aggregate (for now we use AVERAGE by default)
			double uSum = 0.0; 
			for (T it : items)
				uSum += evaluateItem(agent, it);
			
			return uSum/items.size();
		}
	}
	
	protected abstract double evaluateItem (UserAg<T> agent, T item);
	
	public abstract String toString();

}
