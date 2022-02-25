package edu.isistan.pumas.framework.protocols.oneStep;

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
import edu.isistan.pumas.framework.protocols.commons.exceptions.ZeroAgentsInCoordinatorException;
import edu.isistan.pumas.framework.protocols.commons.proposal.AgProposal;
import edu.isistan.pumas.framework.protocols.commons.proposal.ConflictDealAgProposal;
import edu.isistan.pumas.framework.protocols.commons.userAgents.UserAg;
import edu.isistan.pumas.framework.protocols.monotonicConcession.multilateralConcessionStrategy.MultilateralConcessionStrategy;

/*
 * The one-step protocol can easily be extended to the multilateral case: Collect a proposal from each agent and randomly 
 * choose an outcome amongst those proposals that maximise the product of utilities. The obvious strategy, 
 * which is the same as for the two-agent case, is also both stable and efficient. This simplistic solution not withstanding,
 * we still remain interested in the monotonic concession protocol, because —unlike the one-step protocol— it is a 
 * direct formalisation of natural negotiation behaviour." 
 */
public class OneStepCoordinatorAg<T extends SURItem> extends PUMASCoordinatorAg<T>{
	
	private static final Logger logger = LogManager.getLogger(OneStepCoordinatorAg.class);
	
	public OneStepCoordinatorAg(MultilateralConcessionStrategy<T> multilateralConcessionStrategy) {
		super(multilateralConcessionStrategy);
	}

	@Override
	protected boolean checkAgreement(HashMap<String, AgProposal<T>> proposals){
		/* 
		 * By definition of the OneStep protocol given a list of proposals it is always
		 * possible to find an agreement.
		 */
		return true;
	}
	
	/**
	 * 
	 * @param proposals
	 * @return the proposal for which there is an agreement between the agents
	 */
	@Override
	protected AgProposal<T> getAgreement(HashMap<String, AgProposal<T>> proposals){
		HashMap<AgProposal<T>, Double> agreementOn = new HashMap<>();
		List<UserAg<T>> agents = getUserAgContainer().values();
		
		for (AgProposal<T> p : proposals.values()){
			double utilitiesProduct;
			if (agents.isEmpty())
				utilitiesProduct = 0;
			else{
				utilitiesProduct = 1;
				for (UserAg<T> ag : agents)
					utilitiesProduct *= ag.getUtilityFor(p);
			}
			
			if (agreementOn.isEmpty())
				agreementOn.put(p, utilitiesProduct);
			else {
				double currentAgreementUtil = (new ArrayList<Double>(agreementOn.values())).get(0);
				if (currentAgreementUtil == utilitiesProduct)
					agreementOn.put(p, utilitiesProduct);
				else if (currentAgreementUtil < utilitiesProduct){ //the new agreement has a higher utility product => discard the old, save the new
					agreementOn.clear();
					agreementOn.put(p, utilitiesProduct);
				}
			}
		}
		
		List<AgProposal<T>> agreementList = new ArrayList<> (agreementOn.keySet());	
		
		if (agreementList.isEmpty())
			return null; //if there is no agreement returns null
		else{
			if (agreementList.size()>1) //by definition of the protocol if there is more than one agreement =>  select randomly one of them
				Collections.shuffle(agreementList);
			return agreementList.get(0); 
		}			
	}
	
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
		logger.info("Step 1: Inform the agents about the concession strategy they have to use. => "+ this.getMultilateralConcessionStrategy().getClass().getName());
		informConcessionStrategyToAgents();
		
		//Step 2: Collect initial proposals (load the HashMap<AgentID, Proposal>)
		logger.info("Step 2: Collect initial proposals (load the HashMap<AgentID, Proposal>)");
		HashMap<String, AgProposal<T>> proposals = collectInitialProposals();

		logger.info("Step 3: Searching for the agreement");
		//Find the "agreement"
		AgProposal<T> agreement = this.getAgreement(proposals);
		
		if (agreement != null){
			//Save the agreement as the last
			this.setLastAgreement(agreement);
		
			logger.info("Step 4: AGREEMENT reached with => "+ ((this.getLastAgreement() != null)? this.getLastAgreement().toString() : "Last Agreement was NULL"));
			timer.stop();
			return createNegotiationResult(this.getLastAgreement(), timer.getTime());
		}
		else{
			logger.info("Step 4: CONFLICT REACHED.");
			timer.stop();
			return createNegotiationResult(new ConflictDealAgProposal<T>(), timer.getTime()); //conflict
		}
			
		
		 
	}

	@Override
	public String toString() {
		return "OneStepCoordinatorAg [multilateralConcessionStrategy="
				+ multilateralConcessionStrategy + ", lastAgreement="
				+ lastAgreement + ", agContainer=" + agContainer + "]";
	}

	
}
