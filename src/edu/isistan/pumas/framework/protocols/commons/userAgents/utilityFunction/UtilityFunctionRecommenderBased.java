package edu.isistan.pumas.framework.protocols.commons.userAgents.utilityFunction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.isistan.christian.recommenders.sur.SingleUserRecommender;
import edu.isistan.christian.recommenders.sur.datatypes.SURItem;
import edu.isistan.christian.recommenders.sur.datatypes.SURPrediction;
import edu.isistan.christian.recommenders.sur.exceptions.SURException;
import edu.isistan.pumas.framework.protocols.commons.userAgents.UserAg;
/**
 * 
 * @author Christian
 * 
 *	This type of utility function should only be used in Agents that have an item recommender inside
 * @param <T>
 */
public class UtilityFunctionRecommenderBased<T extends SURItem> extends UtilityFunction<T>{
	
	private static final Logger logger = LogManager.getLogger(UtilityFunctionRecommenderBased.class);
	
	protected SingleUserRecommender<T> itemRecSys; //for "computing the utility of the proposals"
	
	public UtilityFunctionRecommenderBased(){
	}
	
	public UtilityFunctionRecommenderBased(SingleUserRecommender<T> itemRecSys){
		this.itemRecSys = itemRecSys;
	}
	
	public void setItemRecSys (SingleUserRecommender<T> itemRecSys){
		this.itemRecSys = itemRecSys;
	}
	
	public double evaluateItem (UserAg<T> agent, T item){
		if (this.itemRecSys == null){
			logger.warn("The itemRecSys of this class is not set. The evaluation will return 0."); //Don't like this but if I throw an exception
			return 0;
		}
		
		SURPrediction<T> p = null;
		try {
			p = itemRecSys.estimatePreference(agent.getRepresentedUser(), item);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		if (p!= null && p.isValid())
			try {
				return itemRecSys.estimateUserRating(p);
			} catch (SURException e) {
				logger.error(e.getMessage());
				return 0;
			}
		else
			return 0;
	}

	@Override
	public String toString() {
		return "UtilityFunctionRecommenderBased []";
	}
	
}
