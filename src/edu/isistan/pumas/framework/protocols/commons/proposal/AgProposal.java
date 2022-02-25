package edu.isistan.pumas.framework.protocols.commons.proposal;

import edu.isistan.christian.recommenders.sur.datatypes.SURItem;
import edu.isistan.pumas.framework.dataTypes.NegotiableItem;

public class AgProposal<T extends SURItem>{

	private static int counter = 0;
	protected int ID;

	NegotiableItem<T> proposedItem;
	
	protected String agentID; //agent who created this proposal	
	protected double utilityValue; //the utility value of the proposal for the agent who created this proposal. 

	public AgProposal(String agentID, NegotiableItem<T> proposedItem, double utility) {
		super();
		this.ID = counter;
		counter++;
		this.agentID = agentID;
		this.proposedItem = proposedItem;
		this.utilityValue = utility;
	}

	

	public int getID() {
		return ID;
	}


	public String getAgentID() {
		return agentID;
	}

	/**
	 * 
	 * @return the item proposed or NULL if the proposal isEmpty or is a conflictDeal proposal
	 */
	public NegotiableItem<T> getItemProposed(){
		return this.proposedItem;
	}

	public double getUtilityValue() {
		return utilityValue;
	}

	/**
	 * 
	 * @return TRUE if after proposing this item we can make a concession and 
	 * propose a new one, and FALSE if we can't do that.
	 */
	public boolean canBeConceded(){
		return true;
	}

	public boolean isEmpty(){
		return false;
	}
	
	public boolean isConflictDeal(){
		return false;
	}

	@Override
	public String toString() {
		return "AgProposal [ID=" + ID + ", agentID=" + agentID + ", utilityValue=" + utilityValue + ", proposedItem="
				+ proposedItem + "]";
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ID;
		result = prime * result + ((agentID == null) ? 0 : agentID.hashCode());
		result = prime * result + ((proposedItem == null) ? 0 : proposedItem.hashCode());
		long temp;
		temp = Double.doubleToLongBits(utilityValue);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AgProposal<?> other = (AgProposal<?>) obj;
		if (ID != other.ID)
			return false;
		if (agentID == null) {
			if (other.agentID != null)
				return false;
		} else if (!agentID.equals(other.agentID))
			return false;
		if (proposedItem == null) {
			if (other.proposedItem != null)
				return false;
		} else if (!proposedItem.equals(other.proposedItem))
			return false;
		if (Double.doubleToLongBits(utilityValue) != Double.doubleToLongBits(other.utilityValue))
			return false;
		return true;
	}
	
}
