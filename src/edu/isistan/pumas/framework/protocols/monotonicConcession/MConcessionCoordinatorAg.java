package edu.isistan.pumas.framework.protocols.monotonicConcession;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.isistan.christian.recommenders.sur.datatypes.SURItem;
import edu.isistan.pumas.framework.dataTypes.NegotiationResult;
import edu.isistan.pumas.framework.protocols.PUMASCoordinatorAg;
import edu.isistan.pumas.framework.protocols.commons.exceptions.NonConcedableCurrentProposalException;
import edu.isistan.pumas.framework.protocols.commons.exceptions.ZeroAgentsInCoordinatorException;
import edu.isistan.pumas.framework.protocols.commons.proposal.AgProposal;
import edu.isistan.pumas.framework.protocols.commons.proposal.ConflictDealAgProposal;
import edu.isistan.pumas.framework.protocols.commons.userAgents.UserAg;
import edu.isistan.pumas.framework.protocols.monotonicConcession.agreementStrategy.AgreementStrategy;
import edu.isistan.pumas.framework.protocols.monotonicConcession.multilateralConcessionStrategy.MultilateralConcessionStrategy;
import edu.isistan.pumas.framework.protocols.monotonicConcession.negotiationStrategy.NegotiationStrategy;

public class MConcessionCoordinatorAg<T extends SURItem> extends PUMASCoordinatorAg<T>{

	private static final Logger logger = LogManager.getLogger(MConcessionCoordinatorAg.class);

	protected AgreementStrategy<T> agreementStrategy; //strategy used to determine whether if an agreement has been reached or not.
	protected NegotiationStrategy<T> negotiationStrategy; //strategy used to select who has to concede 

	public MConcessionCoordinatorAg(AgreementStrategy<T> agreementStrategy,
			NegotiationStrategy<T> negotiationStrategy,
			MultilateralConcessionStrategy<T> multilateralConcessionStrategy) {
		super(multilateralConcessionStrategy);
		this.agreementStrategy = agreementStrategy;
		this.negotiationStrategy = negotiationStrategy;
	}

	/**
	 * 
	 * @param proposals
	 * @return true if there is an agreement (between the agents) for one of the proposals 
	 */
	protected boolean checkAgreement(HashMap<String, AgProposal<T>> proposals){
		for (AgProposal<T> prop : proposals.values())
			if (this.agreementStrategy.checkAgreement(prop, this.getUserAgContainer().values()))
				return true;

		return false;		
	}

	/**
	 * 
	 * @param proposals
	 * @return the proposal for which there is an agreement between the agents or NULL if there is no agreement
	 */
	protected AgProposal<T> getAgreement(HashMap<String, AgProposal<T>> proposals){
		List<AgProposal<T>> agreementOn = new ArrayList<>();
		
		for (AgProposal<T> prop : proposals.values())
			if (this.agreementStrategy.checkAgreement(prop, this.getUserAgContainer().values()))
				agreementOn.add(prop);

		if (agreementOn.size()>1)
			Collections.shuffle(agreementOn);
		return agreementOn.get(0); //if there is no agreement returns null
	}

	
	//--------------------------------------

		
	/**
	 * 
	 * @return
	 * @throws ZeroAgentsInCoordinatorException 
	 */
	public NegotiationResult<T> executeProtocol() throws ZeroAgentsInCoordinatorException{

		if (agContainer.isEmpty()){
			throw new ZeroAgentsInCoordinatorException();
		}
		
		StopWatch timer = new StopWatch();
		timer.start();
		
		//Step 1: Inform the agents about the concession strategy they have to use
		logger.info("Step 1: Inform the agents about the concession strategy they have to use. => "+ getMultilateralConcessionStrategy().getClass().getName());
		informConcessionStrategyToAgents();
		
		//Step 2: Collect initial proposals (load the HashMap<AgentID, Proposal>)
		logger.info("Step 2: Collect initial proposals (load the HashMap<AgentID, Proposal>)");
		HashMap<String, AgProposal<T>> proposals = collectInitialProposals();
//		logger.info("Step 2.a: Initial Proposals: \n "+proposals);

		logger.info("Step 3: Loop until you reach an Agreement or a Conflict.");
		//Step 3: Loop until you reach an Agreement or a Conflict
		while(!checkAgreement(proposals)){

			//Select who has to concede
			logger.info("Step 3.a: Select Agent who has to concede");
			List<UserAg<T>> shouldConcede = this.negotiationStrategy.selectWhoHasToConcede(agContainer.values()); //can be more than one if, for example, there are more than 1 agent with the same willingness to risk conflict value (if using the Zeuthen Strategy)

			if (shouldConcede.isEmpty()){
				logger.info("Step 4: CONFLICT REACHED.");
				timer.stop();
				return createNegotiationResult(new ConflictDealAgProposal<T>(), timer.getTime()); //conflict 
			}				
			else{				
				logger.info("Step 3.b: Agent/s who has to concede [#= "+ shouldConcede.size()+"]=> "+ shouldConcede.toString());
				
				for (UserAg<T> concedingAg : shouldConcede){
					//Make "concedingAg" to concede
					AgProposal<T> newProposal;
					try {
						newProposal = concedingAg.makeConcession();
						
						logger.info("Step 3.c: New Proposal made by the agent => "+newProposal.toString());
						//Update proposals map
						proposals.put(concedingAg.getID(), newProposal);
						
//						//REMOVE THIS or change it -- debug only
//						HashMap<String, Double> utilitiesMap = new HashMap<>();
//						for (UserAg<T> ag : agContainer.values())
//							utilitiesMap.put(ag.getID(), ag.getUtilityFor(newProposal));
//							
//						logger.debug("Utilities over new proposal:"+utilitiesMap);
						
					} catch (NonConcedableCurrentProposalException e) {
						/* This should never happen because we have invoked the method using an agent 
						 * that we know that it can concede but as we need to catch the exception we 
						 * need this catch block.
						 */
					}
				}
			}
		}
		//At this point an agreement should have been found => return it
		this.setLastAgreement(getAgreement(proposals));
		
		logger.info("Step 4: AGREEMENT reached with => "+ ((this.getLastAgreement() != null)? this.getLastAgreement().toString() : "Last Agreement was NULL"));
		timer.stop();
		return createNegotiationResult(this.getLastAgreement(), timer.getTime());
	}

	@Override
	public String toString() {
		return "MonotonicConcessionCoordinatorAg [agreementStrategy="
				+ agreementStrategy + ", negotiationStrategy="
				+ negotiationStrategy + ", multilateralConcessionStrategy="
				+ multilateralConcessionStrategy + ", lastAgreement="
				+ lastAgreement + ", agContainer=" + agContainer + "]";
	}

	
}
