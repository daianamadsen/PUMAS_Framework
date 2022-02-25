package edu.isistan.pumas.framework.protocols.commons.userAgents.concessionCriterion;

import edu.isistan.christian.recommenders.sur.datatypes.SURItem;
import edu.isistan.pumas.framework.protocols.commons.proposal.AgProposal;
import edu.isistan.pumas.framework.protocols.commons.userAgents.UserAg;

public class ConcessionCriterionAND <T extends SURItem> extends ConcessionCriterionComposed<T> {

	@Override
	public boolean isAcceptableProposal(UserAg<T> agent, AgProposal<T> proposal) {
		boolean acceptable = true;
		
		for (int i=0; i<this.criterias.size() && acceptable; i++){
			acceptable = criterias.get(i).isAcceptableProposal(agent, proposal);
		}
			
		return acceptable;
	}

	@Override
	public boolean canConcede(UserAg<T> agent) {
		boolean canConcede = true;
		
		for (int i=0; i<this.criterias.size() && canConcede; i++){
			canConcede = criterias.get(i).canConcede(agent);
		}
			
		return canConcede;
	}

	@Override
	public String toString() {
		return "ConcessionCriterionAND [criterias=" + criterias + "]";
	}
	

}
