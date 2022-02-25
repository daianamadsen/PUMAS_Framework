package edu.isistan.pumas.framework.protocols.commons.proposalsManager.proposalsPool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.isistan.christian.recommenders.sur.datatypes.SURItem;
import edu.isistan.pumas.framework.dataTypes.NegotiableItem;
import edu.isistan.pumas.framework.protocols.commons.exceptions.NothingToProposeException;
import edu.isistan.pumas.framework.protocols.commons.proposal.AgProposal;

public abstract class ProposalsPool <T extends SURItem> {

	protected ArrayList<AgProposal<T>> candidateProposals; //contains the list of candidate proposals the agent can make during the negotiation
	protected HashMap <NegotiableItem<T>, AgProposal<T>> itemsMapper;

	protected AgProposal<T> firstProposal; 
	protected AgProposal<T> currentProposal;  //Last (and current) proposal made by the agent (this is updated when the agent makes a concession)

	public ProposalsPool(){
		candidateProposals = new ArrayList<>();
		itemsMapper = new HashMap<>();
		
		firstProposal = null;
		currentProposal = null;
	}
	
	/**
	 * Adds the proposal 'p' to the pool of candidate proposals of the agent
	 * @param p
	 */
	public void add (AgProposal<T> p){
		itemsMapper.put(p.getItemProposed(), p);
		if (!candidateProposals.contains(p))
			candidateProposals.add(p);
	}

	/**
	 * Removes the proposal 'p' from the pool of candidate proposals of the agent
	 * @param p
	 */
	public void remove (AgProposal<T> p){
		itemsMapper.remove(p.getItemProposed());
		candidateProposals.remove(p); //as the proposal pool can't have duplicated items, using "remove" is sufficient.
	}

	/**
	 * 
	 * @return the actual list of proposals the agent can propose in the next round (candidates)
	 */
	public List<AgProposal<T>> getCandidatesList(){
		return candidateProposals;
	}

	/**
	 * 
	 * @param item
	 * @return the proposal of the agent which has the proposedItem 
	 * attribute equal to 'item' if any, or NULL if there is no proposal that fulfills the search criterion.
	 */
	public AgProposal<T> searchProposalWith (NegotiableItem<T> item){
		return itemsMapper.get(item);
	}

	/**
	 * 
	 * @return true if the pool allows to recycle proposals
	 */
	public abstract boolean allowsProposalsRecycling();
	
	
	/**
	 * @return
	 * @throws NothingToProposeException
	 */
	public AgProposal<T> getFirstProposal() throws NothingToProposeException{
			if (firstProposal == null || firstProposal.isEmpty())
				throw new NothingToProposeException();
			return firstProposal;
	}
	
	/**
	 * 
	 * @return the proposal which is actually being held by the agent
	 * @throws NothingToProposeException 
	 */
	public AgProposal<T> getCurrentProposal() throws NothingToProposeException {
		if (currentProposal == null || currentProposal.isEmpty())
			throw new NothingToProposeException();
		return currentProposal;
	}
		
	/**
	 * 
	 * @param p
	 */
	public void changeCurrentProposal (AgProposal<T> newCurrent){
		
		if (currentProposal == null){ //first proposal
			this.firstProposal = newCurrent; //this is the first proposal the agent made
		}
		else{
			//Depending of the type of ProposalPool (recycler or not recycler) this will re-add the "old current proposal" to the candidates list or not
			this.processOldCurrent(currentProposal);
		}
		
		//Store the newProposal as the current proposal
		currentProposal = newCurrent;
		//Update list of candidates 
		candidateProposals.remove(currentProposal); //currentProposal = newCurrent is not a candidate anymore
	}
	
	/**
	 * Depending of the type of ProposalPool (recycler or not recycler) this will re-add the oldCurrent 
	 * to the candidates list or not
	 * @param oldCurrent
	 */
	protected abstract void processOldCurrent (AgProposal<T> oldCurrent);
	
	/**
	 * Invoked when the agent is going to attempt a refill of the pool (before trying to add items)
	 */
	public abstract void prepareForRefill();
	
	/**
	 * Invoked when we want to start a new negotiation after being finalized a previous one.
	 * It reset the contents of the pool 
	 */
	public void reset(){
		candidateProposals.clear();		
		firstProposal= null;
		currentProposal = null;
	}
	
	/**
	 * 
	 * @return all the proposals in the pool. The proposals added depend on the type of the pool. 
	 * Initially it contains the current proposal (if it's not empty) and the candidateProposals. The firstProposal 
	 * is already inside of the candidateProposals list.  
	 */
	public List<AgProposal<T>> getAllProposals(){
		ArrayList<AgProposal<T>> allProposals = new ArrayList<>();
		if (!currentProposal.isEmpty()){ //to exclude the empty proposal the agents use as temporal proposal when they have nothing to propose
			allProposals.add (currentProposal);
			allProposals.addAll(candidateProposals);
		}
		
		return allProposals;
	}
	
}
