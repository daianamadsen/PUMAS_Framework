package edu.isistan.pumas.framework.protocols.commons.proposal.comparators;

import java.util.Comparator;

import edu.isistan.christian.recommenders.sur.datatypes.SURItem;
import edu.isistan.pumas.framework.protocols.commons.proposal.AgProposal;

public class ProposalUtilityComparator<T extends SURItem> implements Comparator<AgProposal<T>> {
	
	protected boolean ascendingOrder = true;
	
	public ProposalUtilityComparator(boolean ascendingOrder) {
		super();
		this.ascendingOrder = ascendingOrder;
	}

	@Override
	public int compare(AgProposal<T> item1, AgProposal<T> item2) {
		
		if (ascendingOrder)
			return Double.compare(item1.getUtilityValue(),item2.getUtilityValue());
		else
			return (-1)*(Double.compare(item1.getUtilityValue(),item2.getUtilityValue()));
	}

}
