package edu.isistan.pumas.framework.protocols.commons.userAgents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.isistan.christian.recommenders.sur.SingleUserRecommender;
import edu.isistan.christian.recommenders.sur.datatypes.SURItem;
import edu.isistan.christian.recommenders.sur.datatypes.SURRecommendation;
import edu.isistan.christian.recommenders.sur.datatypes.SURUser;
import edu.isistan.christian.recommenders.sur.exceptions.SURException;
import edu.isistan.christian.recommenders.sur.exceptions.SURInexistentUserException;
import edu.isistan.pumas.framework.dataTypes.NegotiableItemSimple;
import edu.isistan.pumas.framework.protocols.commons.exceptions.CacheRefillingException;
import edu.isistan.pumas.framework.protocols.commons.exceptions.NothingToProposeException;
import edu.isistan.pumas.framework.protocols.commons.proposal.AgProposal;
import edu.isistan.pumas.framework.protocols.commons.userAgents.arPunishmentStrategy.AlreadyRatedPunishmentStrategy;
import edu.isistan.pumas.framework.protocols.commons.userAgents.concessionCriterion.ConcessionCriterion;
import edu.isistan.pumas.framework.protocols.commons.userAgents.initialProposalStrategy.InitialProposalStrategy;
import edu.isistan.pumas.framework.protocols.commons.userAgents.proposalAcceptanceStrategy.ProposalAcceptanceStrategy;
import edu.isistan.pumas.framework.protocols.commons.userAgents.utilityFunction.UtilityFunction;

public class UserAgRecommenderBased <T extends SURItem> extends UserAg<T>{

	private static final Logger logger = LogManager.getLogger(UserAgRecommenderBased.class);

	protected SingleUserRecommender<T> itemRecSys; //for "generating" the proposals this agent can make

	//------------------------------ CONSTRUCTOR METHODS
	/**
	 * 
	 * @param myUser the user this agent represents
	 */
	public UserAgRecommenderBased(SURUser myUser, InitialProposalStrategy<T> initialProposalStrategy, ConcessionCriterion<T> concessionCriterion,
			ProposalAcceptanceStrategy<T> proposalAcceptStrategy, SingleUserRecommender<T> itemRecSys, UtilityFunction<T> utilityFunction, 
			AlreadyRatedPunishmentStrategy<T> arPunishmentStrategy, boolean proposalsPoolAllowsRecycling, 
			boolean optReuseNotUsedProposalsEnabled, boolean optUtilityCacheEnabled){

		super(myUser, initialProposalStrategy, concessionCriterion, proposalAcceptStrategy, utilityFunction, arPunishmentStrategy, 
				proposalsPoolAllowsRecycling, optReuseNotUsedProposalsEnabled, optReuseNotUsedProposalsEnabled);
		
		this.itemRecSys = itemRecSys;	

	}

	public UserAgRecommenderBased(SURUser myUser, InitialProposalStrategy<T> initialProposalStrategy, 
			ConcessionCriterion<T> concessionCriterion, ProposalAcceptanceStrategy<T> proposalAcceptStrategy, 
			SingleUserRecommender<T> itemRecSys, UtilityFunction<T> utilityFunction, AlreadyRatedPunishmentStrategy<T> arPunishmentStrategy,
			int proposalsPoolMaxSize, int maxProposalsPoolRefillsAllowed, boolean proposalsPoolIsRefillAllowed,
			boolean proposalsPoolAllowsRecycling, boolean optNotUsedProposalsEnabled, boolean optUtilityCacheEnabled){
		super(myUser, initialProposalStrategy, concessionCriterion, proposalAcceptStrategy,	utilityFunction,
				arPunishmentStrategy, proposalsPoolMaxSize, maxProposalsPoolRefillsAllowed, 
				proposalsPoolIsRefillAllowed, proposalsPoolAllowsRecycling, optNotUsedProposalsEnabled,
				optUtilityCacheEnabled);
		
		this.itemRecSys = itemRecSys;	

	}

	//------------------------------ PROPOSALS POOL REFILL RELATED METHODS
	
	protected List<AgProposal<T>> buildCandidateProposals(int n) throws Exception{
		List<AgProposal<T>> newCandidates = new ArrayList<>();
		
		List<SURRecommendation<T>> recommendations = itemRecSys.recommend(getRepresentedUser(), n);
		Collections.sort(recommendations);
		
		/* Transform the recommendations to candidate proposals of the agent and filter the non-valid recommendation 
		 * (and the non-acceptable proposals for improving performance by avoiding try to concede to something which is 
		 * not acceptable by the criterion held by the agent)
		 */
		
		for (SURRecommendation<T> r : recommendations){
			if (r.isValid()){
				AgProposal<T> prop = new AgProposal<T>(getID(),
						new NegotiableItemSimple<>(r.getRecommendedItem()),
						r.getPredictedUserRating()); //Create a proposal using the recommendation
				newCandidates.add(prop);
			}
		}
		
		return newCandidates;
	}

	//------------------------------ INFORMATION LEAK RELATED METHODS

//	/**
//	 * 
//	 * @return the proportion of proposals we have exposed during the current negotiation
//	 * (Proportion = #Proposals exposed (made)/#total of proposals the agent could have proposed)
//	 * #total of proposals the agent could propose = all the possible proposals the agent could propose, 
//	 * even those which were not added to the proposalsPool during the negotiation but could have been added 
//	 * if necessary.
//	 */
	public double getProposalsRevealedPercentage(){

		//All the non ranked items were candidates to be proposed by the agent (in spite of the fact of they being filtered or not by the agent concession criteria). 
		int nonRankedItems = 0;
		try {
			nonRankedItems = itemRecSys.getItemsNotRatedBy(getRepresentedUser().getID()).size();
		} catch (SURInexistentUserException e) {
			//do nothing, THIS SHOULD NEVER HAPPEN
		}
		if (nonRankedItems != 0)
			return ((double)this.statsManager.getProposalsRevealed()/(double)nonRankedItems); 
		else
			return 0.0;
	}

	/**
	 * 
	 * @return the proportion of items from whom the agent had to reveal the utility value during
	 * the current negotiation 
	 * (Proportion = #items from whom the utility was revealed/#total of items in the recommender)
	 */
	public double getUtilitiesRevealedPercentage(){
		int allItemsCount = itemRecSys.getAllItems().size();
		if (allItemsCount != 0)
			return ((double)this.statsManager.getItemsWithUtilityRevealedMap().keySet().size()/(double)allItemsCount); //we can use the proposals total here because for every item we can propose we will have a proposal
		else
			return 0.0;
	}

	//------------------------- GETTERS AND SETTERS FOR CONFIGURATIONS

	//**************************** STRATEGIES AND CRITERIA RELATED METHODS

	/**
	 * 
	 * @return the ItemRecommenderSystem the agent uses for "generating" the proposals he can make.
	 */
	public SingleUserRecommender<T> getItemRecSys() {
		return itemRecSys;
	}

	/**
	 * 
	 * @param itemRecSys the ItemRecommenderSystem the agent should use for "generating" the proposals he can make.
	 * @param refillCache if TRUE then the current proposalsCache (the pool of proposals from which the agent 
	 * select the proposals he can make if he has to concede) will be cleared and then refilled with items recommended
	 * by the new recommender system. If FALSE the current cache will not be cleared and refilled. In this case, the new recommender system
	 * will be use the next time the cache gets empty and the agent has to refill it by himself.
	 */
	public void setItemRecSys(SingleUserRecommender<T> itemRecSys, boolean refillCache) {
		this.itemRecSys = itemRecSys;
		if (refillCache){
			try {
				refillProposalsPool();
			} catch (CacheRefillingException e) {
				logger.error(e.getMessage());
			}
		}
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
	
	public void reset(boolean fullReset){
		super.reset(fullReset);
		if (!OPT_REUSE_NOT_USED_PROPOSALS_ENABLED || fullReset){
			//Make the recommender to forget what he had recommended to you. The items which are blacklisted (in the ProposalsManager) will be used to discard
			// candidates recommended by the recommender as they were the result (agreement) of a past negotiation in the sequence 
			try {
				this.getItemRecSys().forgetPastRecommendations(this.representedUser);
			} catch (SURException e) {
				logger.warn (e); //should never happen, just in case
			}

		}
	}

}
