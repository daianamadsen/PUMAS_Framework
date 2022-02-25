package edu.isistan.pumas.framework.protocols.commons.userAgents.arPunishmentStrategy;

import edu.isistan.christian.recommenders.sur.datatypes.SURItem;
import edu.isistan.pumas.framework.dataTypes.NegotiableItem;
import edu.isistan.pumas.framework.protocols.commons.userAgents.UserAg;

public class ARPSMinimumSatisfaction<T extends SURItem> implements AlreadyRatedPunishmentStrategy<T> {

	protected double minimumSatisfactionLevel = 0.0; //DEFAULT
	
	public ARPSMinimumSatisfaction(double minimumSatisfactionLevel) {
		this.minimumSatisfactionLevel = minimumSatisfactionLevel;
	}

	public ARPSMinimumSatisfaction() {
	}
	
	public double getMinimumSatisfactionLevel() {
		return minimumSatisfactionLevel;
	}

	public void setMinimumSatisfactionLevel(double minimumSatisfactionLevel) {
		this.minimumSatisfactionLevel = minimumSatisfactionLevel;
	}

	public double computePunishmentFor(double originalUtilityValue, UserAg<T> agent, NegotiableItem<T> nItem) {
		if (originalUtilityValue > minimumSatisfactionLevel)
			return 0.0;
		else
			return 1.0 * originalUtilityValue; //acts like taboo
	}

	@Override
	public String toString() {
		return "ARPMinimumSatisfaction [minimumSatisfactionLevel=" + minimumSatisfactionLevel + "]";
	}

}
