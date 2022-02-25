package edu.isistan.pumas.framework.protocols.commons.userAgents.arPunishmentStrategy;

import edu.isistan.christian.recommenders.sur.datatypes.SURItem;
import edu.isistan.pumas.framework.dataTypes.NegotiableItem;
import edu.isistan.pumas.framework.protocols.commons.userAgents.UserAg;

public interface AlreadyRatedPunishmentStrategy<T extends SURItem>{

	public double computePunishmentFor (double originalUtilityValue, UserAg<T> agent, NegotiableItem<T> nItem);

	public String toString();
}
