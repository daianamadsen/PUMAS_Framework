package edu.isistan.pumas.framework.protocols.commons.userAgents.arPunishmentStrategy;

import edu.isistan.christian.recommenders.sur.SingleUserRecommender;
import edu.isistan.christian.recommenders.sur.datatypes.SURItem;
import edu.isistan.pumas.framework.dataTypes.NegotiableItem;
import edu.isistan.pumas.framework.protocols.commons.userAgents.UserAg;

/**
 * 
 * @author Christian
 *
 * @param <T>
 * 
 * This strategy applies the same kind of punishment than the original strategy ({@link ARPStrategyFlexible}) but only if the user 
 * satisfaction over the NegotiationItem is below a give minimum satisfaction level. If the user satisfaction (measured by 
 * the "originalUtilityValue") is above said level, the punishment will only be the half or what it should have been. 
 * 
 * By default the flexibility level is 1 (completely flexible). It can be changed using the proper set method.
 * By default the minimum satisfaction level is 0 (every proposal with utility >= than 0 will satisfy the criteria). 
 * These settings can be changed using the proper set methods.
 * 
 */
public class ARPStrategyFlexiblePlus<T extends SURItem> extends ARPStrategyFlexible<T>{

	protected double minimumSatisfactionLevel = 0.0; //DEFAULT
	
	public ARPStrategyFlexiblePlus(){
		super();
	}

	public ARPStrategyFlexiblePlus(double flexibilityLevel, SingleUserRecommender<T> itemRecSys, double minimumSatisfactionLevel) {
		super(flexibilityLevel, itemRecSys);
		this.minimumSatisfactionLevel = minimumSatisfactionLevel;
	}
	
	public double getMinimumSatisfactionLevel() {
		return minimumSatisfactionLevel;
	}

	public void setMinimumSatisfactionLevel(double minimumSatisfactionLevel) {
		this.minimumSatisfactionLevel = minimumSatisfactionLevel;
	}

	public double computePunishmentFor(double originalUtilityValue, UserAg<T> agent, NegotiableItem<T> nItem) {
		double basePunishment = super.computePunishmentFor(originalUtilityValue, agent, nItem);
		if (basePunishment > 0  && originalUtilityValue > minimumSatisfactionLevel)
			return  basePunishment/2;
		else
			return basePunishment;
	}

	@Override
	public String toString() {
		return "ARPStrategyFlexiblePlus [flexibilityLevel=" + flexibilityLevel + ", minimumSatisfactionLevel="
				+ minimumSatisfactionLevel + "]";
	}
	
}
