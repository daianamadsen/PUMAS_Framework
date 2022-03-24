package edu.isistan.pumas.framework.protocols.commons.userAgents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.isistan.christian.recommenders.sur.datatypes.SURItem;
import edu.isistan.christian.recommenders.sur.datatypes.SURUser;
import edu.isistan.pumas.framework.dataTypes.NegotiableItem;
import edu.isistan.pumas.framework.protocols.commons.exceptions.CacheRefillingException;
import edu.isistan.pumas.framework.protocols.commons.exceptions.NoProposalsAvailableException;
import edu.isistan.pumas.framework.protocols.commons.exceptions.NonConcedableCurrentProposalException;
import edu.isistan.pumas.framework.protocols.commons.exceptions.NothingToProposeException;
import edu.isistan.pumas.framework.protocols.commons.proposal.AgProposal;
import edu.isistan.pumas.framework.protocols.commons.proposal.EmptyAgProposal;
import edu.isistan.pumas.framework.protocols.commons.proposalsManager.ProposalsManager;
import edu.isistan.pumas.framework.protocols.commons.userAgents.arPunishmentStrategy.AlreadyRatedPunishmentStrategy;
import edu.isistan.pumas.framework.protocols.commons.userAgents.concessionCriterion.ConcessionCriterion;
import edu.isistan.pumas.framework.protocols.commons.userAgents.initialProposalStrategy.InitialProposalStrategy;
import edu.isistan.pumas.framework.protocols.commons.userAgents.proposalAcceptanceStrategy.ProposalAcceptanceStrategy;
import edu.isistan.pumas.framework.protocols.commons.userAgents.utilityFunction.UtilityFunction;
import edu.isistan.pumas.framework.protocols.monotonicConcession.multilateralConcessionStrategy.MultilateralConcessionStrategy;

public abstract class UserAg <T extends SURItem>{

	private static final Logger logger = LogManager.getLogger(UserAg.class);


	private final static AtomicInteger count = new AtomicInteger(0);  //to avoid concurrent creation of groups with the same ID and also concurrent modifications of the static variable

	protected int userAgIDNumber;

	//Strategies (Criterias)
	protected InitialProposalStrategy<T> initialProposalStrategy;  //for deciding what item to propose the first time
	protected ConcessionCriterion<T> concessionCriterion; //for deciding when this agent should concede or not (iff the current proposal is a ConcedableProposal)
	protected MultilateralConcessionStrategy<T> multilateralConcessionType; //for selecting the proposals when: making the initial proposal, selecting the next item to propose to the other agents
	protected ProposalAcceptanceStrategy<T> proposalAcceptStrategy; //for deciding if the agent agrees with a proposal (invoked by the coordinator while deciding if there is an agreement over a certain proposal)

	//Utility related stuff
	protected Optional<AlreadyRatedPunishmentStrategy<T>> arPunishmentStrategy;
	protected UtilityFunction<T> utilityFunction;
	protected HashMap<NegotiableItem<T>, Double> utilityCache; //(to avoid re-computing them everytime)

	//User represented by the agent
	protected SURUser representedUser;

	protected Double assertivenessFactor = -1.0;
	protected Double cooperativenessFactor = -1.0;
	protected HashMap<String, Double> relationshipsFactor = new HashMap<String, Double>();

	//Proposals Pool (PPool) refilling
	protected int PPOOL_MAX_SIZE = 20;
	protected int MAX_PPOOL_REFILLS_ALLOWED = Integer.MAX_VALUE;
	protected boolean PPOOL_IS_REFILL_ALLOWED = true;
	protected int pPoolRefillsCount = 0; 
	protected boolean initialPPoolRefillDone = false;
	
	
	//Agent stats
	protected AgStatsManager<T> statsManager;

	//Concessions
//	protected int concessionsMadeCount = 0;

	//ProposalsManager
	protected ProposalsManager<T> proposalsManager;
	protected boolean PPOOL_ALLOWS_PROPOSALS_RECYCLING = false;

	//For Statistics about information leakage
//		protected int leakedInfo_proposalsMade; //every time you concede + the first proposal you need to do a +1 on this counter
//		protected HashMap<NegotiableItem<T>, Integer> leakedInfo_utilityValue; //stores information about how many times the utility of an item has been asked by the other agents

	//Optimizations
	protected boolean OPT_REUSE_NOT_USED_PROPOSALS_ENABLED = true;
	protected boolean OPT_UTILITY_CACHE_ENABLED = true;

	//------------------------------ CONSTRUCTOR METHODS
	/**
	 * 
	 * @param myUser the user this agent represents
	 */
	public UserAg(SURUser myUser, InitialProposalStrategy<T> initialProposalStrategy, ConcessionCriterion<T> concessionCriterion, 
			ProposalAcceptanceStrategy<T> proposalAcceptStrategy, UtilityFunction<T> utilityFunction,
			AlreadyRatedPunishmentStrategy<T> arPunishmentStrategy, boolean proposalsPoolAllowsRecycling, 
			boolean optReuseNotUsedProposalsEnabled, boolean optUtilityCacheEnabled){

		this.userAgIDNumber = count.getAndIncrement();

		this.representedUser = myUser;
		this.initialProposalStrategy = initialProposalStrategy;
		this.concessionCriterion = concessionCriterion;
		this.proposalAcceptStrategy = proposalAcceptStrategy;

		this.utilityFunction = utilityFunction;
		if (arPunishmentStrategy != null)
			this.arPunishmentStrategy = Optional.of(arPunishmentStrategy);
		else
			this.arPunishmentStrategy = Optional.empty();

		this.utilityCache = new HashMap<>();

		this.statsManager = new AgStatsManager<>();
		
		this.PPOOL_ALLOWS_PROPOSALS_RECYCLING = proposalsPoolAllowsRecycling;
		this.proposalsManager = new ProposalsManager<T>(PPOOL_ALLOWS_PROPOSALS_RECYCLING);

		this.OPT_REUSE_NOT_USED_PROPOSALS_ENABLED = optReuseNotUsedProposalsEnabled;
		this.OPT_UTILITY_CACHE_ENABLED = optUtilityCacheEnabled;	

	}

	public UserAg(SURUser myUser, InitialProposalStrategy<T> initialProposalStrategy, ConcessionCriterion<T> concessionCriterion, 
			ProposalAcceptanceStrategy<T> proposalAcceptStrategy, UtilityFunction<T> utilityFunction, 
			AlreadyRatedPunishmentStrategy<T> arPunishmentStrategy, int proposalsPoolMaxSize, int maxProposalsPoolRefillsAllowed, 
			boolean proposalsPoolIsRefillAllowed, boolean proposalsPoolAllowsRecycling, boolean optNotUsedProposalsEnabled, 
			boolean optUtilityCacheEnabled){
		this(myUser, initialProposalStrategy, concessionCriterion, proposalAcceptStrategy,
				utilityFunction, arPunishmentStrategy, proposalsPoolAllowsRecycling,
				optNotUsedProposalsEnabled, optUtilityCacheEnabled);

		this.PPOOL_MAX_SIZE = proposalsPoolMaxSize;
		this.MAX_PPOOL_REFILLS_ALLOWED = maxProposalsPoolRefillsAllowed;
		this.PPOOL_IS_REFILL_ALLOWED = proposalsPoolIsRefillAllowed;
	}
	
	/**
	 * 
	 * @return the user represented by the agent
	 */
	public SURUser getRepresentedUser() {
		return representedUser;
	}
	
	/**
	 * 
	 * @return the agent ID
	 */
	public String getID(){
		return "userAg_"+userAgIDNumber;
	}

	//------------------------------ PROPOSALS POOL REFILL RELATED METHODS
	
	protected abstract List<AgProposal<T>> buildCandidateProposals(int n) throws Exception;

	/**
	 * Add more items to the pool (keeping the ones that are currently there)
	 * @throws CacheRefillingException
	 */
	protected void refillProposalsPool() throws CacheRefillingException{
		logger.debug ("I ("+this.getID()+") am refilling my proposals pool (Refill #"+getProposalsPoolRefillsCount()+")");
		
		//Prepare the pool for refill
		proposalsManager.preparePoolForRefill();

		//If we can't refill => throw an exception 
		if (initialPPoolRefillDone && (!PPOOL_IS_REFILL_ALLOWED || getProposalsPoolRefillsCount() >= MAX_PPOOL_REFILLS_ALLOWED))
			throw new CacheRefillingException();

		//Ask the recommender a list of recommendations and put them in the cache
		try {
			int added = 0;
			List<AgProposal<T>> candidates = this.buildCandidateProposals(PPOOL_MAX_SIZE);
			
			for (AgProposal<T> prop: candidates){
				//Add item to cache
				utilityCache.put(prop.getItemProposed(), prop.getUtilityValue());
				
				if (concessionCriterion.isAcceptableProposal(this, prop)){
					this.proposalsManager.addToPool(prop);
					added++;
				}
			}

			// If we couldn't add any new items => throw an exception
			if (added == 0)
				throw new CacheRefillingException();

			//If you could add some items => you made a successful refill (one more). CAREFUL HERE!! (REVISE IF THIS IS DESIRABLE) if you couldn't add new items => the counter will not increase!!
			if (!initialPPoolRefillDone)
				initialPPoolRefillDone = true;
			else
				pPoolRefillsCount++; //the first time it doesn't count as a refill

		} catch (Exception e) { //Maybe because PPOOL_MAX_SIZE > the amount of recommendable items for the user!
			throw new CacheRefillingException(e);
		}
	}

	//------------------------------ PROPOSAL's RELATED METHODS
	/**
	 * 
	 * @return the first proposal made by the agent 
	 */
	public AgProposal<T> getOriginalProposal() throws NothingToProposeException{
		return proposalsManager.getOriginalProposal();
	}

	/**
	 * 
	 * @return the proposal which is actually being held by the agent
	 * @throws NothingToProposeException 
	 */
	public AgProposal<T> getCurrentProposal() throws NothingToProposeException {
		return proposalsManager.getCurrentProposal();
	}

	public void registerAgreementOn (AgProposal<T> p){
		proposalsManager.addToBlacklist(p.getItemProposed());
	}

	//------------------------------ STATS
	/**
	 * 
	 * @return the amount of concessions made by the agent
	 */
	public int getConcessionsMadeCount(){
		return this.statsManager.getConcessionsMade(); 
	}
	
	/**
	 * 
	 * @return the amount of proposals made by the agent
	 */
	public int getProposalsMadeCount(){
		return this.statsManager.getProposalsMade(true); 
	}
	
	/**
	 * 
	 * @return the amount of proposals revealed by the agent
	 */
	public int getProposalsRevealedCount(){
		return this.statsManager.getProposalsRevealed(); 
	}
	
	/**
	 * @param distinct  If {@code distinct} if {@literal TRUE} it will ignore the amount of times the item proposed 
	 * was accepted by the agent (ex. with recurring proposals in agents that recycle old proposals an agent can accept a proposal more than once).
	 * Otherwise, every occurrence of an acceptance will be count towards the amount of proposals accepted (i.e: a proposal accepted 3 
	 * times will count as 3 instead of 1).
	 * @return the amount of proposals accepted by the agent.
	 */
	public int getProposalsAcceptedCount(boolean distinct) {
		return this.statsManager.getProposalsAccepted(distinct);
	}
	
	/**
	 * @param distinct If {@code distinct} if {@literal TRUE} it will ignore the amount of times the item proposed was accepted by the agent (ex. with recurring 
	 * proposals in agents that recycle old proposals an agent can accept a proposal more than once). Otherwise, every occurrence of an acceptance will be count 
	 * towards the amount of proposals accepted (i.e: a proposal accepted 3 times will count as 3 instead of 1).
	 * @return a hashmap indicating how many proposals from each other agent were accepted.
	 */
	public HashMap<String, Integer> getProposalsAcceptedPerAgent(boolean distinct) {
		return this.statsManager.getProposalsAcceptedPerAgent(distinct);
	}
	
	/**
	 * @param distinct 
	 * @return the amount of proposals rejected by the agent. If {@code distinct} if {@literal TRUE} it will ignore the amount of times the item proposed 
	 * was rejected by the agent.
	 * Otherwise, every occurrence of an acceptance will be count towards the amount of proposals rejected (i.e: a proposal rejected 3 
	 * times will count as 3 instead of 1).
	 */
	public int getProposalsRejectedCount(boolean distinct) {
		return this.statsManager.getProposalsRejected(distinct);
	}
	
	/**
	 * @param distinct If {@code distinct} if {@literal TRUE} it will ignore the amount of times the item proposed was rejected by the agent (ex. with recurring 
	 * proposals in agents that recycle old proposals an agent can accept a proposal more than once). Otherwise, every occurrence of an reject will be count towards
	 *  the amount of proposals accepted (i.e: a proposal accepted 3 times will count as 3 instead of 1).
	 * @return a hashmap indicating how many proposals from each other agent were accepted.
	 */
	public HashMap<String, Integer> getProposalsRejectedPerAgent(boolean distinct) {
		return this.statsManager.getProposalsRejectedPerAgent(distinct);
	}

	/**
	 * 
	 * @return the proportion of proposals we have exposed during the current negotiation
	 * (Proportion = #Proposals exposed (made)/#total of proposals the agent could have proposed)
	 * #total of proposals the agent could propose = all the possible proposals the agent could propose, 
	 * even those which were not added to the proposalsPool during the negotiation but could have been added 
	 * if necessary.
	 */
	public abstract double getProposalsRevealedPercentage();

	/**
	 * 
	 * @return the proportion of items from whom the agent had to reveal the utility value during
	 * the current negotiation 
	 * (Proportion = #items from whom the utility was revealed/#total of items in the recommender)
	 */
	public abstract double getUtilitiesRevealedPercentage();

	//------------------------------ MONOTONIC CONCESSION PROTOCOL METHODS

	/**
	 * 
	 * @return the initial proposal the agent will make which could be a normal proposal (a concedable one) 
	 * or a "conflict deal proposal" (non-concedable proposal with utility 0)
	 * 
	 * Regarding to the "empty deal proposal": it's just that, an empty proposal (it has no proposed item). This way the agent will be part of any 
	 * negotiation but as his proposal has utility 0 he will always agree if the agents choose the proposal of 
	 * another agent. Also the agent shouldn't be able to make concessions either.
	 * @throws NothingToProposeException 
	 */
	public AgProposal<T> makeInitialProposal() throws NothingToProposeException{
		AgProposal<T> initialProp = null;
		boolean nothingToPropose = false;
		boolean retry = true;

		while (initialProp == null && retry){
			try {
				initialProp = initialProposalStrategy.makeInitialProposal(proposalsManager.getCandidateProposalsFromPool());
			} catch (NoProposalsAvailableException e) {
				//Cache was empty OR the proposals in the cache didn't fulfill the mutilateralConcessionType criteria
				try { 
					refillProposalsPool();
				} catch (CacheRefillingException e1) {
					//refill failed => stop!
					retry = false;
					nothingToPropose = true;
					initialProp = new EmptyAgProposal<T>(getID()); //"conflict deal"
				}
			}
		}

		if (nothingToPropose){
			/*Create a non concedable and empty proposal with utility 0 and set it as your current proposal 
			 *It is used for comparing with the other proposals and MUST be discarded at the end of the negotiation.
			 */
			initialProp = new EmptyAgProposal<T>(this.getID());
			//Updates the agent's internal state
			proposalsManager.changeCurrentProposal(initialProp);
			throw new NothingToProposeException();
		}

		//Updates the agent's internal state
		proposalsManager.changeCurrentProposal(initialProp);
		
		//Update the stats of the agent
		this.statsManager.registerProposalMade(initialProp);
		//register an information leak
		this.statsManager.registerProposalRevealed(initialProp);

		return initialProp; 
	}


	/**
	 * This method let know which is the next item the agent will propose (using the multilateral concession strategy)
	 * if he has to make a concession during the current negotiation
	 * @return the next proposal the agent will make which could be a normal proposal, a conflict deal proposal OR NULL if the current proposal can't be conceded
	 * 
	 * Regarding to the "conflict deal proposal": this way the next thing the agent can propose is the worst thing for him:
	 * something with utility 0. All the agents can propose this when they ran out of proposals. They will 
	 * propose this (or not) depending on their "ConcessionCriteria". But careful, a "conflict deal proposal" is
	 * the lower the agent can fall: after making a concession and proposing this (if it happens), they will
	 * not be able to make a concession again.
	 */
	public AgProposal<T> peekNextProposal() throws NonConcedableCurrentProposalException{

		AgProposal<T> currentProposal;
		try {
			currentProposal = proposalsManager.getCurrentProposal();
		} catch (NothingToProposeException e) {
			throw new NonConcedableCurrentProposalException();
		}

		if (currentProposal.canBeConceded()){ //"currentProposal" should never be null at this point of the code
			AgProposal<T> nextProposal = null;
			boolean retry = true;

			while (nextProposal == null && retry){
				try {
					nextProposal = multilateralConcessionType.getNextItemToPropose(this, proposalsManager.getCandidateProposalsFromPool()); //attempt to get the next
				} catch (NoProposalsAvailableException e) {
					//Cache was empty OR the proposals in the cache didn't fulfill the mutilateralConcessionType criteria
					try {
						//a this.cachedProposals.clear() is not needed because 
						refillProposalsPool();
					} catch (CacheRefillingException e1) {
						//refill failed => stop!
						retry = false;
						nextProposal = new EmptyAgProposal<T>(getID()); //"conflict deal"
					}
				} catch (NonConcedableCurrentProposalException e){
					//we should never reach this because as the currentProposal should be concedable we are never going to execute the "getNextItemToPropose()" method when the proposal is nonConcedable
					throw e;
				}
			}

			//We are exposing another proposal => register the information leak (update the agent stats)
			this.statsManager.registerProposalRevealed(nextProposal);

			//We don't have to save anything in this method because is just allowing us to spy what will be the next item the agent will propose		
			return nextProposal; 
		}
		else
			throw new NonConcedableCurrentProposalException();
	}

	/**
	 * 	
	 * @param proposal
	 * @return the utility value of a proposal according to the represented user criteria.
	 * 
	 */
	public double getUtilityFor(AgProposal<T> proposal){
		double utility = 0.0;
		NegotiableItem<T> item = proposal.getItemProposed();
		if (utilityCache.containsKey(item))
			utility = utilityCache.get(item);
		else{
			utility = utilityFunction.evaluate(this, proposal);
			utilityCache.put(item, utility); //update the cache (add the unknown utility)
		}

		//Check if the "already rated penalty" should be applied
		if (!proposal.getAgentID().equals(this.getID())){ //if it was proposed by this agent
			if (arPunishmentStrategy.isPresent() && !proposal.isEmpty() && !proposal.isConflictDeal())
				utility -= arPunishmentStrategy.get().computePunishmentFor(utility, this, proposal.getItemProposed());
		}

		//TODO CHECK this, we are considering everything a leak (no matter if it's our own proposal or not
		//we are going to leak information about the utility of the item of the proposal (to another agent)
		this.statsManager.registerUtilityRevealedFor(item);

		return utility;
	}

	public double getAssertivenessFactor() {
		return this.assertivenessFactor;
	}

	public void setAssertivenessFactor(double assertivenessFactor) {
		this.assertivenessFactor = assertivenessFactor;
	}

	public double getCooperativenessFactor() {
		return this.cooperativenessFactor;
	}

	public void setCooperativenessFactor(double cooperativenessFactor) {
		this.cooperativenessFactor = cooperativenessFactor;
	}

	public HashMap<String, Double> getRelationshipsFactor() {
		return this.relationshipsFactor;
	}

	public void setRelationshipsFactor(HashMap<String, Double> relationshipsFactor) {
		this.relationshipsFactor = relationshipsFactor;
	}

	/**
	 * 
	 * @param proposal
	 * @return true if the proposal is accepted by the agent according to his ProposalAcceptance strategy
	 */
	public boolean accepts(AgProposal<T> proposal){
		if (proposal.getAgentID().equals(this.getID()))
			return true; //the agent will always accept his own proposal
		
		boolean accepts = proposalAcceptStrategy.accepts(this, proposal);
		if (accepts)
			this.statsManager.registerProposalAccepted(proposal);
		else
			this.statsManager.registerProposalRejected(proposal);
		return accepts;
	}

	/**
	 * 
	 * @return TRUE if the agent can concede, FALSE otherwise
	 */
	public boolean canConcede(){
		AgProposal<T> currentProposal;
		try {
			currentProposal = proposalsManager.getCurrentProposal();
		} catch (NothingToProposeException e) {
			return false;
		}
		/*
		 * If "getCurrentProposal().canBeConceded() = TRUE" => the last thing the agent proposed (his current proposal) 
		 * was an "empty proposal" and this indicates he had nothing to propose so now he should not concede because 
		 * he has nothing more to propose. The proposal with utility value equal to 0 was the lower he could go.
		 */
		return currentProposal.canBeConceded() && concessionCriterion.canConcede(this); //"currentProposal" should never be null at this point of the code
	}


	/**
	 * The agent has to make a concession, so it will see what item he should propose using the according to
	 * the MultilateralConcessionType strategy, then if there is an item to propose (a 
	 * CacheRefillingException wasn't thrown), it will select that item and propose it, updating his own 
	 * internal state in the way.
	 * @return the item the agent is proposing after making a concession
	 */
	public AgProposal<T> makeConcession() throws NonConcedableCurrentProposalException{

		AgProposal<T> newProposal = null;

		AgProposal<T> currentProposal;
		try {
			currentProposal = proposalsManager.getCurrentProposal();
		} catch (NothingToProposeException e) {
			throw new NonConcedableCurrentProposalException();
		}

		if (currentProposal.canBeConceded()){
			//Get the next item to propose (if there is any)
			newProposal = peekNextProposal();
		}
		else
			throw new NonConcedableCurrentProposalException();

		//Updates the agent's internal state
		proposalsManager.changeCurrentProposal(newProposal);
		
		//Update the count of concessions made and register the proposal we are making when conceding
		this.statsManager.registerConcessionMade();
		this.statsManager.registerProposalMade(newProposal);
		//We are exposing another proposal => register the information leak
		this.statsManager.registerProposalRevealed(newProposal);

		return newProposal;
	}


	public void reset(boolean fullReset){
		//reset the agent stats
		this.statsManager.reset();
		
		this.pPoolRefillsCount = 0;

		if (!OPT_UTILITY_CACHE_ENABLED || fullReset){
			/* If the optimization is enabled the cache will be kept among several sequential negotiations. 
			 * if the cache is not cleared when the agent is reseted, the first negotitation will take more time (as a lot of utilities should be computed, 
			 * but the following negotiations will be much faster because of the precomputed utilities.
			 */
			utilityCache.clear();
		}
		
		if (!OPT_REUSE_NOT_USED_PROPOSALS_ENABLED || fullReset) {
			//Reset the pool (first proposal, current, clear all the lists)
			proposalsManager.resetPool();
		}
		else {
			//Backup the proposals in the pool (candidates, current, and the others depending on the pool type)
			ArrayList<AgProposal<T>> allProps = new ArrayList<>(proposalsManager.getAllProposalsFromPool());

			//Reset the pool (first proposal, current, clear all the lists)
			proposalsManager.resetPool();

			//Restore all the proposals (they will be all available to be selected in the next negotiation)
			proposalsManager.addToPool(allProps);
		}

	}



	//------------------------- GETTERS AND SETTERS FOR CONFIGURATIONS

	//**************************** STRATEGIES AND CRITERIA RELATED METHODS

	/**
	 * 
	 * @return the concession criteria the agent uses to decide when he can concede (iff the current proposal is concedable)
	 */
	public ConcessionCriterion<T> getConcessionCriterion() {
		return concessionCriterion;
	}

	/**
	 * 
	 * @param concessionCriteria
	 */
	public void setConcessionCriterion(ConcessionCriterion<T> concessionCriterion) {
		this.concessionCriterion = concessionCriterion;
	}


	/**
	 * 
	 * @return the strategy used by the agent for selecting determining which of the candidate proposals he should propose the first time (immediately after the negotiation starts).
	 */
	public InitialProposalStrategy<T> getInitialProposalStrategy() {
		return initialProposalStrategy;
	}

	/**
	 * 
	 * @param initialProposalStrategy
	 */
	public void setInitialProposalStrategy(
			InitialProposalStrategy<T> initialProposalStrategy) {
		this.initialProposalStrategy = initialProposalStrategy;
	}

	/**
	 * 
	 * @return the multilateral concession type the agent is using
	 */
	public MultilateralConcessionStrategy<T> getMultilateralConcessionType() {
		return multilateralConcessionType;
	}

	/**
	 * 
	 * @param multilateralConcessionType the multilateral concession type the agent will use when:
	 * - making the initial proposal
	 * - selecting the next "proposal" to make to the other agents
	 */
	public void setMultilateralConcessionType(MultilateralConcessionStrategy<T> multilateralConcessionType) {
		this.multilateralConcessionType = multilateralConcessionType;
	}
	
	/**
	 * @return the proposal acceptance stragegy followed by the agent
	 */
	public ProposalAcceptanceStrategy<T> getProposalAcceptStrategy() {
		return proposalAcceptStrategy;
	}

	/**
	 * 
	 * @param proposalAcceptStrategy the strategy used by the agent to decide whether he accepts or not a proposal received 
	 * (used when the coordinator is deciding if there is an agreement among the agents over a proposal) 
	 */
	public void setProposalAcceptStrategy(ProposalAcceptanceStrategy<T> proposalAcceptStrategy) {
		this.proposalAcceptStrategy = proposalAcceptStrategy;
	}	

	//**************************** GENERAL METHODS

	@Override
	public String toString() {
		AgProposal<T> currentProposal;
		try {
			currentProposal = getCurrentProposal();
		} catch (NothingToProposeException e) {
			currentProposal = null;
		}

		return "UserAg [userAgIDNumber=" + userAgIDNumber
				+ ", representedUser=" + representedUser + ", #concessionsMade="
				+ getConcessionsMadeCount() + ", #proposals (in pool)=" + proposalsManager.getCandidateProposalsFromPool().size()
				+ ", currentProposal=" + ((currentProposal != null)? currentProposal.toString() : "NOT SET YET")
				+ "]";
		//				+ ", concessionCriteria=" + ((concessionCriteria != null)? concessionCriteria.getClass().getSimpleName() : "NOT SET YET")
		//				+ ", multilateralConcessionType=" + ((multilateralConcessionType != null)? multilateralConcessionType.getClass().getSimpleName(): "NOT SET YET")
		//				+ ", itemRecSys=" + ((itemRecSys != null)? itemRecSys.getClass().getSimpleName() : "NOT SET YET") + "]";
	}

	//**************************** PROPOSALS POOL REFILL RELATED METHODS

	public int getProposalsPoolRefillsCount(){
		return pPoolRefillsCount;
	}

	public int getProposalsPoolMaxSize() {
		return PPOOL_MAX_SIZE;
	}


	public void setProposalsPoolMaxSize(int proposalsPoolMaxSize) {
		PPOOL_MAX_SIZE = proposalsPoolMaxSize;
	}

	public int getMaxProposalsPoolRefillsAllowed() {
		return MAX_PPOOL_REFILLS_ALLOWED;
	}

	public void setMaxProposalsPoolRefillsAllowed(int maxProposalsPoolRefillsAllowed) {
		MAX_PPOOL_REFILLS_ALLOWED = maxProposalsPoolRefillsAllowed;
	}

	public boolean isProposalsPoolRefillAllowed() {
		return PPOOL_IS_REFILL_ALLOWED;
	}

	public void setProposalsPoolRefillAllowed(boolean proposalsPoolIsRefillAllowed) {
		this.PPOOL_IS_REFILL_ALLOWED = proposalsPoolIsRefillAllowed;
	}
}
