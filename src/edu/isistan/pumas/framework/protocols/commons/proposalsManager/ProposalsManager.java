package edu.isistan.pumas.framework.protocols.commons.proposalsManager;

import java.util.ArrayList;
import java.util.List;

import edu.isistan.christian.recommenders.sur.datatypes.SURItem;
import edu.isistan.pumas.framework.dataTypes.NegotiableItem;
import edu.isistan.pumas.framework.protocols.commons.exceptions.NothingToProposeException;
import edu.isistan.pumas.framework.protocols.commons.proposal.AgProposal;
import edu.isistan.pumas.framework.protocols.commons.proposalsManager.proposalsPool.ProposalsPool;
import edu.isistan.pumas.framework.protocols.commons.proposalsManager.proposalsPool.ProposalsPoolNonRecycler;
import edu.isistan.pumas.framework.protocols.commons.proposalsManager.proposalsPool.ProposalsPoolRecycler;

public class ProposalsManager <T extends SURItem>{

	protected ProposalsPool<T> proposalsPool;
	
	//Blacklist (items you should not propose)
	protected List<NegotiableItem<T>> blacklistedItems;
		
	public ProposalsManager(boolean allowsRecycling){
		if (allowsRecycling)
			proposalsPool = new ProposalsPoolRecycler<T>();
		else
			proposalsPool = new ProposalsPoolNonRecycler<T>();
		
		blacklistedItems = new ArrayList<>();
	}
	
	
	public void addToPool (AgProposal<T> p){
		if (!isBlacklisted(p.getItemProposed())) //tira error acá!!!!! ni idea en qué item!!! es un null pointer exception!
			proposalsPool.add(p);
	}
	
	public void addToPool (List<AgProposal<T>> proposals){
		for (AgProposal<T> p : proposals)
			this.addToPool(p);
	}
	
	public void removeFromPool (AgProposal<T> p){
		proposalsPool.remove(p);
	}
	
	public List<AgProposal<T>> getCandidateProposalsFromPool(){
		return proposalsPool.getCandidatesList();
	}
	
	public List<AgProposal<T>> getAllProposalsFromPool(){
		return proposalsPool.getAllProposals();
	}
	
	/**
	 * @return the First proposal which was set as current
	 * @throws NothingToProposeException
	 */
	public AgProposal<T> getOriginalProposal() throws NothingToProposeException{
			return proposalsPool.getFirstProposal();
	}
	
	/**
	 * 
	 * @return the proposal which is actually being held by the agent
	 * @throws NothingToProposeException 
	 */
	public AgProposal<T> getCurrentProposal() throws NothingToProposeException {
		return proposalsPool.getCurrentProposal();
	}
	
	public void changeCurrentProposal (AgProposal<T> newCurrent){
		proposalsPool.changeCurrentProposal(newCurrent);
	}
	
	public void resetPool(){
		proposalsPool.reset();
	}
	
	public void preparePoolForRefill(){
		proposalsPool.prepareForRefill();
	}
	
	public AgProposal<T> searchProposalWith (NegotiableItem<T> item){
		return proposalsPool.searchProposalWith(item);
	}
	
	//------------------------- BLACKLIST MANAGEMENT METHODS
	
	//----- REGISTRA EL AGREEMENT
	
	public void addToBlacklist(NegotiableItem<T> item){
		if (!blacklistedItems.contains(item)){
			blacklistedItems.add(item);
			AgProposal<T> prop = proposalsPool.searchProposalWith(item);
			if (prop != null)
				proposalsPool.remove(prop);
		}
	}
	
	public void removefromBlacklist (NegotiableItem<T> item){
		blacklistedItems.remove(item);
	}

	public List<NegotiableItem<T>> getBlacklistedItems(){
		return blacklistedItems;
	}
	
	public boolean isBlacklisted (NegotiableItem<T> item){
		return blacklistedItems.contains(item);
	}


	@Override
	public String toString() {
		return "ProposalsManager [proposalsPool=" + proposalsPool + ", #blacklistedItems=" + blacklistedItems.size() + "]";
	}
	
	

	
}
