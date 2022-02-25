package edu.isistan.pumas.framework.protocols.commons.userAgents.arPunishmentStrategy;

import edu.isistan.christian.recommenders.sur.datatypes.SURItem;

public class ARPStrategyTaboo<T extends SURItem> extends ARPStrategyFlexible<T> {

	public ARPStrategyTaboo() {
		super(0);
	}

	@Override
	public String toString() {
		return "ARPStrategyTaboo []";
	}
	
}
