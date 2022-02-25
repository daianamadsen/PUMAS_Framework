package edu.isistan.pumas.framework.protocols.commons.userAgents.arPunishmentStrategy;

import edu.isistan.christian.recommenders.sur.datatypes.SURItem;
import edu.isistan.pumas.framework.dataTypes.NegotiableItem;
import edu.isistan.pumas.framework.protocols.commons.userAgents.UserAg;

public class ARPStrategyEasyGoing<T extends SURItem> implements AlreadyRatedPunishmentStrategy<T>{

	public ARPStrategyEasyGoing() {
	
	}
	
	@Override
	public double computePunishmentFor(double originalUtilityValue, UserAg<T> agent, NegotiableItem<T> nItem) {
		return 0; //This strategy doesn't ever punish
	}
	
	@Override
	public String toString() {
		return "ARPStrategyEasyGoing []";
	}
	
}
