package edu.isistan.pumas.framework.protocols;

import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.isistan.christian.recommenders.sur.datatypes.SURItem;
import edu.isistan.pumas.framework.dataTypes.NegotiationResult;
import edu.isistan.pumas.framework.protocols.commons.exceptions.NothingToProposeException;
import edu.isistan.pumas.framework.protocols.commons.exceptions.ZeroAgentsInCoordinatorException;
import edu.isistan.pumas.framework.protocols.commons.proposal.AgProposal;
import edu.isistan.pumas.framework.protocols.commons.userAgents.UserAg;
import edu.isistan.pumas.framework.protocols.commons.userAgents.UserAgContainer;
import edu.isistan.pumas.framework.protocols.monotonicConcession.multilateralConcessionStrategy.MultilateralConcessionStrategy;

public abstract class PUMASCoordinatorAg<T extends SURItem> {

	private static final Logger logger = LogManager.getLogger(PUMASCoordinatorAg.class);
		
	protected MultilateralConcessionStrategy<T> multilateralConcessionStrategy; //all the agent will concede using this strategy
	protected AgProposal<T> lastAgreement; //we need to save it for the execution of the reset method and the way it works.
	
	protected UserAgContainer<T> agContainer;
	

	public PUMASCoordinatorAg(MultilateralConcessionStrategy<T> multilateralConcessionStrategy){
		this.multilateralConcessionStrategy = multilateralConcessionStrategy;
		agContainer = new UserAgContainer<T>();
		lastAgreement = null;
	}
	
	public MultilateralConcessionStrategy<T> getMultilateralConcessionStrategy(){
		return multilateralConcessionStrategy;
	}
	
	protected AgProposal<T> getLastAgreement(){
		return lastAgreement;
	}
	
	protected void setLastAgreement (AgProposal<T> lastAgreement){
		this.lastAgreement = lastAgreement;
	}
	
	public UserAgContainer<T> getUserAgContainer(){
		return this.agContainer;
	}
	
	/**
	 * Adds the agents to the coordinator. 
	 * These agents are the ones that will participate in the negotiation process (which is guided by the coordinator)
	 */
	public void addAgents (List<UserAg<T>> agents){
		for (UserAg<T> agent : agents)
			agContainer.put(agent.getID(), agent);
	}
	//------------------------------------------- PROTOCOL RELATED METHODS
	
	/**
	 * 
	 * @return the list of proposals made by the agents along with the agentID of the agent who made it
	 */
	protected HashMap<String, AgProposal<T>> collectInitialProposals(){
		HashMap<String, AgProposal<T>> proposals = new HashMap<>();

		for (UserAg<T> ag : getUserAgContainer().values()){
			AgProposal<T> agInitialProposal;
			try {
				agInitialProposal = ag.makeInitialProposal();
				
				/* Add the proposal to the list. Careful here!! If an agent has nothing to propose => will 
				 * make a special type of proposal: an empty proposal (which has utility=0 and can't be conceded).
				 */
				proposals.put(ag.getID(), agInitialProposal); 
				logger.info(">> Initial Proposal Collected [AGENT= "+ag.getID()+", PROPOSAL MADE= "+agInitialProposal+"]");
			} catch (NothingToProposeException e) {
				logger.info(">> Initial Proposal Collected [AGENT= "+ag.getID()+", PROPOSAL MADE= "+"NONE (CAUSE: "+e.getMessage()+")]");
			}
		}

		return proposals;
	}
	
	/**
	 * 
	 * @param proposals
	 * @return true if there is an agreement (between the agents) for one of the proposals 
	 */
	protected abstract boolean checkAgreement(HashMap<String, AgProposal<T>> proposals);
	
	/**
	 * 
	 * @param proposals
	 * @return the proposal for which there is an agreement between the agents or NULL if there is no agreement
	 */
	protected abstract AgProposal<T> getAgreement(HashMap<String, AgProposal<T>> proposals);
	
	/**
	 * 
	 */
	protected void informConcessionStrategyToAgents(){
		//Inform what are the agents in the negotiation to the multilateralConcessionStrategy 
		this.multilateralConcessionStrategy.setAgentsInNegotiation(getUserAgContainer().values());
		
		//Inform the agents about the multilateral concession strategy to be used
		for (UserAg<T> a : getUserAgContainer().values())
			a.setMultilateralConcessionType(this.multilateralConcessionStrategy);
	}
	
	
	/**
	 * 
	 * @param agreement
	 * @return
	 */
	protected NegotiationResult<T> createNegotiationResult (AgProposal<T> negResult, long executionTimeInMillis){
		NegotiationResult<T> result = new NegotiationResult<T>();
		result.setNegotiationResult(negResult);
		result.setExecutionTimeInMillis(executionTimeInMillis);
		
		for (UserAg<T> ag : getUserAgContainer().values()){
			result.addAgentInfo(ag.getID(), ag.getRepresentedUser(), ag.getUtilityFor(negResult), 
					ag.getConcessionsMadeCount(),
					ag.getProposalsMadeCount(),
					ag.getProposalsRevealedPercentage(),
					ag.getUtilitiesRevealedPercentage(),
					ag.getProposalsAcceptedPerAgent(true), //register only the distincts
					ag.getProposalsRejectedPerAgent(true)); //register only the distincts
		}
				
		return result;
	}
	
	/**
	 * 
	 * @return
	 * @throws ZeroAgentsInCoordinatorException 
	 */
	public abstract NegotiationResult<T> executeProtocol() throws ZeroAgentsInCoordinatorException;
	
	/**
	 * This method is used when we want to make more than one consecutive negotiation. 
	 * None of the agents should even consider proposing an item that 
	 */
	public void reset(boolean fullReset){
		
		//Reset Agents:
		for (UserAg<T> ag: getUserAgContainer().values()){
			ag.reset(fullReset);
			if (lastAgreement != null && !fullReset)
				ag.registerAgreementOn(lastAgreement);
		}
			
		
	}

	@Override
	public String toString() {
		return "PUMASCoordinatorAg [multilateralConcessionStrategy="
				+ multilateralConcessionStrategy + ", lastAgreement="
				+ lastAgreement + ", agContainer=" + agContainer + "]";
	}
	
	
	
	
}
