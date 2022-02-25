package edu.isistan.pumas.framework.protocols.commons.userAgents.concessionCriterion;

import java.util.ArrayList;
import java.util.List;

import edu.isistan.christian.recommenders.sur.datatypes.SURItem;
import edu.isistan.pumas.framework.protocols.commons.proposal.AgProposal;
import edu.isistan.pumas.framework.protocols.commons.userAgents.UserAg;

public abstract class ConcessionCriterionComposed<T extends SURItem> implements ConcessionCriterion<T>{
	
	List<ConcessionCriterion<T>> criterias;
	

	public ConcessionCriterionComposed(List<ConcessionCriterion<T>> criterias) {
		super();
		this.criterias = criterias;
	}
	
	public ConcessionCriterionComposed() {
		super();
		this.criterias = new ArrayList<ConcessionCriterion<T>>();
	}
	
	public void addConcessionCriteria (ConcessionCriterion<T> c){
		if (!criterias.contains(c))
			criterias.add(c);
	}

	@Override
	public abstract boolean isAcceptableProposal(UserAg<T> agent, AgProposal<T> proposal);

	@Override
	public abstract boolean canConcede(UserAg<T> agent);

}
