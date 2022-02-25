package edu.isistan.pumas.framework.dataTypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.isistan.christian.recommenders.sur.datatypes.SURItem;
import edu.isistan.christian.recommenders.sur.datatypes.SURUser;
import edu.isistan.pumas.framework.protocols.commons.proposal.AgProposal;

public class NegotiationResult <T extends SURItem>{
	private AgProposal<T> result;
	private long executionTimeInMillis;

	private HashMap<String, SURUser> usersInNegotiation; //key: agentID, Value= user represented by the agent
	private HashMap<String, Integer> concessionsMade;  //key: agentID
	private HashMap<String, Double> utilityOfNResults; //key: agentID, Value= the utility value of the result negotiation (from the agent point of view)
	private HashMap<String, Integer> proposalsMade; //key: agentID
	private HashMap<String, Double> infoLeakProposalsRevealedPercentage; //key: agentID
	private HashMap<String, Double> infoLeakUtilitiesRevealed; //key: agentID
	private HashMap<String, HashMap<String, Integer>> proposalsAccepted; //key: agentID, value= a hashmap specifying how many proposals the agent has accepted from each of the other agents
	private HashMap<String, HashMap<String, Integer>> proposalsRejected; //key: agentID, value= a hashmap specifying how many proposals the agent has rejected from each of the other agents
		
	public NegotiationResult(){
		this.usersInNegotiation = new HashMap<>();
		this.concessionsMade = new HashMap<>();
		this.utilityOfNResults = new HashMap<>();
		this.infoLeakProposalsRevealedPercentage = new HashMap<>();
		this.infoLeakUtilitiesRevealed = new HashMap<>();
		this.proposalsMade = new HashMap<>();
		this.proposalsAccepted = new HashMap<>();
		this.proposalsRejected = new HashMap<>();
		
		executionTimeInMillis = 0;
	}
	
	public NegotiationResult(long executionTimeInMillis){
		this();
		this.executionTimeInMillis = executionTimeInMillis;
	}

	public AgProposal<T> getResultProposal(){
		return result;
	}

	public void setNegotiationResult(AgProposal<T> result){
		this.result = result;
	}
	
	public boolean wasConflict(){
		return result.isConflictDeal();
	}
	
	public void addAgentInfo(String agentID, SURUser representedUser, double utilityOfNegotiationResult, 
			int concessionsMadeByAgent, int proposalsMade, double proposalsRevealedPercentage,
			double utilitiesRevealedPercentage, HashMap<String, Integer> proposalsAcceptedPerAgent, HashMap<String, Integer> proposalsRejectedPerAgent){
		this.usersInNegotiation.put(agentID, representedUser);
		this.concessionsMade.put(agentID, concessionsMadeByAgent);
		this.utilityOfNResults.put(agentID, utilityOfNegotiationResult);
		this.infoLeakProposalsRevealedPercentage.put(agentID, proposalsRevealedPercentage);
		this.infoLeakUtilitiesRevealed.put(agentID, utilitiesRevealedPercentage);
		this.proposalsMade.put(agentID, proposalsMade);
		this.proposalsAccepted.put(agentID,  proposalsAcceptedPerAgent);
		this.proposalsRejected.put(agentID,  proposalsRejectedPerAgent);
	}
	
	public long getExecutionTimeInMillis() {
		return executionTimeInMillis;
	}

	public void setExecutionTimeInMillis(long executionTimeInMillis) {
		this.executionTimeInMillis = executionTimeInMillis;
	}

	public List<String> getIDOfAgentsInNegotiation(){
		return new ArrayList<>(usersInNegotiation.keySet()); //any of the hashmaps will have all the ids of the agents involved in the negotiation
	}
	
	public String getAgentIDLinkedTo (String userID){
		String aID = "";
		List<String> agentIDs = new ArrayList<> (usersInNegotiation.keySet()); 
		boolean stop = false;
		
		for (int i=0; i< agentIDs.size() && !stop; i++){
			stop = usersInNegotiation.get(agentIDs.get(i)).getID().equals(userID);
			if (stop)
				aID = agentIDs.get(i);
		}

		return aID;
	}
		
	public SURUser getUserRepresentedBy(String agentID){
		return usersInNegotiation.get(agentID);
	}
	
	public int getConcessionsMadeBy (String agentID){
		return (concessionsMade.containsKey(agentID)? concessionsMade.get(agentID) : 0);
	}
	
	public double getUtilityValueOfResultFor (String agentID){
		return (utilityOfNResults.containsKey(agentID)? utilityOfNResults.get(agentID) : 0);
	}

	public double getProposalsRevealedPercentage(String agentID){
		return (infoLeakProposalsRevealedPercentage.containsKey(agentID)? infoLeakProposalsRevealedPercentage.get(agentID) : 0);
	}
	
	public double getUtilitiesRevealedPercentage(String agentID){
		return (infoLeakUtilitiesRevealed.containsKey(agentID)? infoLeakUtilitiesRevealed.get(agentID) : 0);
	}
	
	public int getProposalsMade(String agentID){
		return (proposalsMade.containsKey(agentID)? proposalsMade.get(agentID) : 0);
	}
	
	public int getProposalsAccepted(String agentID) {
		int accepted = 0;
		for (String otherAgID : proposalsAccepted.get(agentID).keySet())
			accepted += proposalsAccepted.get(agentID).get(otherAgID);
		
		return accepted;
	}
	
	public HashMap<String, Integer> getProposalsAcceptedMap(String agentID) {
		return proposalsAccepted.get(agentID);
	}
	
	public int getProposalsRejected (String agentID) {
		int rejected = 0;
		for (String otherAgID : proposalsRejected.get(agentID).keySet())
			rejected += proposalsRejected.get(agentID).get(otherAgID);
		
		return rejected;
	}
	
	public HashMap<String, Integer> getProposalsRejectedMap(String agentID) {
		return proposalsRejected.get(agentID);
	}
	
	
	public String getAgentInfo(String agentID){
		return "UserAg [ID= "+agentID+", Represented User= "+getUserRepresentedBy(agentID).toString()
				+", UtilityValueOfResult = "+getUtilityValueOfResultFor(agentID)
				+", #concessionsMade= "+getConcessionsMadeBy(agentID)
				+", #proposalsMade= "+getProposalsMade(agentID)
				+", #proposalsAccepted (count)= "+getProposalsAccepted(agentID)
				+", #proposalsRejected (count)= "+getProposalsRejected(agentID)
				+", % Proposals Revealed (info leak)= "+getProposalsRevealedPercentage(agentID)
				+", % Utilities Revealed (info leak)= "+getUtilitiesRevealedPercentage(agentID)
				+"]";
	}
	
	public List<SURUser> getUsersInNegotiation(){
		return new ArrayList<SURUser>(usersInNegotiation.values());
	}
	
	public String toString(){
		String results = "";
		results+= "\n**************************************** RESULTS ****************************************";
		results+= "\nNEGOTIATION RESULT: "+ ((result.isConflictDeal())? "CONFLICT": "AGREEMENT ON => "+result.toString());
		results+= "\nAGENTS INFO SUMMARY: ";
		for (String agID : usersInNegotiation.keySet()){
			results+= "\n  * "+getAgentInfo(agID);
		}
		results+= "\n*****************************************************************************************";
		return results;
	}
	
}
