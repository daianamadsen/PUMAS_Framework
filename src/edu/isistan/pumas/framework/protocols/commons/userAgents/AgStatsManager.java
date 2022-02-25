package edu.isistan.pumas.framework.protocols.commons.userAgents;

import java.util.HashMap;

import edu.isistan.christian.recommenders.sur.datatypes.SURItem;
import edu.isistan.pumas.framework.dataTypes.NegotiableItem;
import edu.isistan.pumas.framework.protocols.commons.proposal.AgProposal;

public class AgStatsManager <T extends SURItem>{

	//Concessions
	private int concessionsMade;
	
	//Proposals
	/**
	 * the items the agent has proposed (either in the first round of negotiation or by concession
	 */
	HashMap<NegotiableItem<T>, Integer> proposalsMade;
	
	/**
	 * The proposals that were ACCEPTED
	 * * key= otherAgentID (the one that generated the proposal received and accepted), value= a hashmap (with key=the proposal accepted, value= the amount of times it was accepted)
	 */
	HashMap<String, HashMap<AgProposal<T>, Integer>> proposalsAccepted;
	/**
	 * The proposals that were REJECTED
	 * * key= otherAgentID (the one that generated the proposal received and accepted), value= List of proposals accepted from this agent
	 */
	HashMap<String, HashMap<AgProposal<T>, Integer>> proposalsRejected;

	//For Statistics about information leakage
	/**
	 * Counts the amounts of proposals that were revealed (by proposing them) to the other agents so as to be able to
	 * track the information leak incurred during the negotiation
	 */
//	private int proposalsRevealed; //every time you concede + the first proposal you need to do a +1 on this counter
	/**
	 * Stores information about how many times the utility of an item has been revealed to
	 * the other agents (when asked by them)
	 */
	private HashMap<NegotiableItem<T>, Integer> itemsUtilityRevealed;
	
	/**
	 * The items the agent has proposed
	 */
	HashMap<NegotiableItem<T>, Integer> proposalsRevealed;
	
	public AgStatsManager () {
		this.concessionsMade = 0;
		this.proposalsMade = new HashMap<>();
		this.proposalsAccepted = new HashMap<>();
		this.proposalsRejected = new HashMap<>();
		this.proposalsRevealed = new HashMap<>();
		this.itemsUtilityRevealed = new HashMap<>();
	}
	
	public void reset() {
		this.concessionsMade = 0;
		this.proposalsMade.clear();
		this.proposalsAccepted.clear();
		this.proposalsRejected.clear();
		this.proposalsRevealed.clear();
		this.itemsUtilityRevealed.clear();
	}
	
	//---------------------- Getters
	public int getConcessionsMade() {
		return concessionsMade;
	}

	/**
	 * 
	 * @param distinct if {@literal TRUE} this will only count the proposals accepted ignoring that some of 
	 * them may have been accepted more than once. If {@literal FALSE} the total of accepted proposals will also take into account the amount of times that each proposal
	 * was accepted. Ex: if we have two proposals A and B, A accepted 1 time and B accepted 3 times, then if  {@code distinct} is {@literal TRUE} the method will 
	 * return 2, but if is {@literal FALSE} it will return 4 (1+3)
	 * @return the amount of proposals accepted by the agent
	 */
	public int getProposalsAccepted(boolean distinct) {
		int allAccepted = 0;
		for (String otherAgID : this.proposalsAccepted.keySet()) {
			allAccepted += this.proposalsAccepted.get(otherAgID).size();
			if (!distinct) {
				for (AgProposal<T> p : this.proposalsAccepted.get(otherAgID).keySet())
					allAccepted += this.proposalsAccepted.get(otherAgID).get(p); //add to the counter all the times the proposal was accepted
			}
		}
		
		return allAccepted;
	}

	/**
	 * 
	 * @param distinct if {@literal TRUE} this will only count the proposals accepted ignoring that some of 
	 * them may have been accepted more than once. If {@literal FALSE} the total of accepted proposals will also take into account the amount of times that each proposal
	 * was accepted. Ex: if an agent1 has accepted two proposals A and B sent by ag2, A accepted 1 time and B accepted 3 times, then if {@code distinct} is {@literal TRUE} the method will 
	 * return the hashmap {"agent2": 2} (two proposals received from "agent2" were accepted), but if is {@literal FALSE} it will return {"agent2": 4} (1+3)
	 * @return a hashmap indicating how many proposals were accepted specifying the author (who created) of the proposal
	 */
	public HashMap<String, Integer> getProposalsAcceptedPerAgent(boolean distinct) {
		HashMap<String, Integer> freqs = new HashMap<>();
		for (String otherAgID : this.proposalsAccepted.keySet()) {
			int accepted = 0;
			if (distinct)
				accepted = this.proposalsAccepted.get(otherAgID).size();
			else {
				for (AgProposal<T> p : this.proposalsAccepted.get(otherAgID).keySet()) {
					accepted += this.proposalsAccepted.get(otherAgID).get(p);
				}
			}
			freqs.put(otherAgID, accepted);
		}
		return freqs;
	}
	
	/**
	 * 
	 * @param distinct if {@literal TRUE} this will only count the proposals accepted ignoring that some of 
	 * them may have been rejected more than once. If {@literal FALSE} the total of rejected proposals will also take into account the amount of times that each proposal
	 * was rejected. Ex: if we have two proposals A and B, A rejected 1 time and B rejected 3 times, then if  {@code distinct} is {@literal TRUE} the method will 
	 * return 2, but if is {@literal FALSE} it will return 4 (1+3)
	 * @return the amount of proposals rejected by the agent
	 */
	public int getProposalsRejected(boolean distinct) {
		int allRejected = 0;
		for (String otherAgID : this.proposalsRejected.keySet()) {
			allRejected += this.proposalsRejected.get(otherAgID).size();
			if (!distinct) {
				for (AgProposal<T> p : this.proposalsRejected.get(otherAgID).keySet())
					allRejected += this.proposalsRejected.get(otherAgID).get(p); //add to the counter all the times the proposal was accepted
			}
		}
		
		return allRejected;
	}
	
	/**
	 * 
	 * @param distinct if {@literal TRUE} this will only count the proposals rejected ignoring that some of 
	 * them may have been rejected more than once. If {@literal FALSE} the total of rejected proposals will also take into account the amount of times that each proposal
	 * was rejected. Ex: if an agent1 has rejected two proposals A and B sent by ag2, A rejected 1 time and B rejected 3 times, then if {@code distinct} is {@literal TRUE} the method will 
	 * return the hashmap {"agent2": 2} (two proposals received from "agent2" were rejected), but if is {@literal FALSE} it will return {"agent2": 4} (1+3)
	 * @return a hashmap indicating how many proposals were rejected specifying the author (who created) of the proposal
	 */
	public HashMap<String, Integer> getProposalsRejectedPerAgent(boolean distinct) {
		HashMap<String, Integer> freqs = new HashMap<>();
		for (String otherAgID : this.proposalsRejected.keySet()) {
			int rejected = 0;
			if (distinct)
				rejected = this.proposalsRejected.get(otherAgID).size();
			else {
				for (AgProposal<T> p : this.proposalsRejected.get(otherAgID).keySet()) {
					rejected += this.proposalsRejected.get(otherAgID).get(p);
				}
			}
			freqs.put(otherAgID, rejected);
		}
		return freqs;
	}
	
	public int getProposalsMade(boolean distinct) {
		if (distinct)
			return proposalsMade.size();
		else {
			int allMade = 0;
			for (NegotiableItem<T> item : this.proposalsMade.keySet())
				allMade += proposalsMade.get(item);
			return allMade;
		}
	}
		
	
	public int getProposalsRevealed() {
		return this.getProposalsRevealed(true);
	}
	
	public int getProposalsRevealed(boolean distinct) {
		if (distinct)
			return proposalsRevealed.size();
		
		int allRevealed = 0;
		for (NegotiableItem<T> item : this.proposalsRevealed.keySet())
			allRevealed += proposalsRevealed.get(item);
		return allRevealed;
	}
		
	public HashMap<NegotiableItem<T>, Integer> getItemsWithUtilityRevealedMap() {
		return new HashMap<NegotiableItem<T>, Integer> (itemsUtilityRevealed);
	}
	
	public int getItemsWithUtilityRevealedCount() {
		return itemsUtilityRevealed.size();
	}

	//---------------------- Setters
	public void registerConcessionMade() {
		this.concessionsMade++;
	}
	
	public void registerProposalMade(AgProposal<T> proposal) {
		this.proposalsMade.compute(proposal.getItemProposed(), (key, oldValue) -> oldValue == null ? 1 : oldValue + 1);
	}
	
	public void registerProposalAccepted(AgProposal<T> proposal) {
		if (!proposalsAccepted.containsKey(proposal.getAgentID()))
			proposalsAccepted.put(proposal.getAgentID(), new HashMap<AgProposal<T>, Integer>()); //create the entry
		
		//If the proposal is already in the map add 1 to the count, else start the count in 1
		proposalsAccepted.get(proposal.getAgentID()).compute(proposal, (key, oldValue) -> oldValue == null ? 1 : oldValue + 1);
	}
	
	public void registerProposalRejected(AgProposal<T> proposal) {
		if (!proposalsRejected.containsKey(proposal.getAgentID()))
			proposalsRejected.put(proposal.getAgentID(), new HashMap<AgProposal<T>, Integer>()); //create the entry
		
		//If the proposal is already in the map add 1 to the count, else start the count in 1
		proposalsRejected.get(proposal.getAgentID()).compute(proposal, (key, oldValue) -> oldValue == null ? 1 : oldValue + 1);
	}
	
	public void registerProposalRevealed (AgProposal<T> proposal) {
		this.proposalsRevealed.compute(proposal.getItemProposed(), (key, oldValue) -> oldValue == null ? 1 : oldValue + 1);
	}
	
	public void registerUtilityRevealedFor (NegotiableItem<T> item) {
		this.itemsUtilityRevealed.compute(item, (key, oldValue) -> oldValue == null ? 1 : oldValue + 1);
	}
}
