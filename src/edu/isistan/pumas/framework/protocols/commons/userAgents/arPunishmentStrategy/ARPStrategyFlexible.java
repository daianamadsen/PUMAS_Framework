package edu.isistan.pumas.framework.protocols.commons.userAgents.arPunishmentStrategy;

import java.util.ArrayList;
import java.util.List;

import edu.isistan.christian.recommenders.sur.SingleUserRecommender;
import edu.isistan.christian.recommenders.sur.datatypes.SURItem;
import edu.isistan.christian.recommenders.sur.exceptions.SURException;
import edu.isistan.pumas.framework.dataTypes.NegotiableItem;
import edu.isistan.pumas.framework.protocols.commons.userAgents.UserAg;


/**
 * 
 * @author Christian
 *
 * @param <T>
 *  
 * This strategy applies a punishment which is proportional to the flexibility value regarding
 * receiving a proposal with an already rated item. The higher the flexibility the lower the punishment is.
 * 
 * Ex: in the movies domain, receiving a proposal with an already rated item would be like being proposed to watch 
 * an already watched movie, which will not be well seen by the users. If the flexibility level is high the user will
 * be happy about watching the movie again, but not as happy as watching a new movie that fits with his interests.
 * On the contrary, a lower flexibility means that the user will not be happy about watching movies that he had already 
 * watched, and therefore the punishment will be high.
 * 
 * PARAMETERS:
 * - flexibilityLevel. If not set, by default the flexibility level is 1 (completely flexible). This can be changed using 
 *   the proper set method.
 * - itemRecSys (required to know if the user has rated the item)
 * 
 */
public class ARPStrategyFlexible<T extends SURItem> implements AlreadyRatedPunishmentStrategy<T>{

	protected double flexibilityLevel = 1.0; //DEFAULT
	protected SingleUserRecommender<T> itemRecSys; //for "computing the utility of the proposals"

	public ARPStrategyFlexible(){
	}

	public ARPStrategyFlexible(double flexibilityLevel){
		this.flexibilityLevel = flexibilityLevel; 
	}

	public ARPStrategyFlexible(double flexibilityLevel, SingleUserRecommender<T> itemRecSys) {
		this.flexibilityLevel = flexibilityLevel;
	}

	public double getFlexibilityLevel() {
		return flexibilityLevel;
	}

	public void setFlexibilityLevel(double flexibilityLevel) {
		this.flexibilityLevel = flexibilityLevel;
	}

	public void setItemRecSys (SingleUserRecommender<T> itemRecSys){
		this.itemRecSys = itemRecSys;
	}

	protected boolean hasRatedItem (UserAg<T> agent, T item){
		try {
			return itemRecSys.hasPreferenceOver(agent.getRepresentedUser(),item);
		} catch (SURException e) {
			return false;
		}
	}

	/**  
	 * @param originalUtilityValue
	 * @param agent
	 * @param nItem
	 * @return the amount of utility that has to be deducted from the {@code originalUtilityValue} when the agent 
	 * {@code agent} computes the utility of a proposal containing the item {@code nItem} and one or more of the 
	 * elements/items within {@code nItem} were already rated by the user in the past. 
	 * If {@code nItem} is composed by more than one item then the punishment will be applied only if the proportion of
	 * already rated items (with respect of the total of items contained by the {@code nItem} surpasses 
	 * the {@code flexibilityLevel} set. 
	 */
	public double computePunishmentFor(double originalUtilityValue, UserAg<T> agent, NegotiableItem<T> nItem){		
		List<T> items = new ArrayList<>(nItem.getItems());
		if (items.isEmpty())
			return 0; //can only happen if nItem is of the type NegotiableItemComposed and it has no items inside
		else if (items.size() == 1){
			if (hasRatedItem(agent, nItem.getItems().get(0))){
				double punishment = 1-flexibilityLevel;
				return originalUtilityValue * punishment; //is negative because this modifier reduces de utility if the user has already rated the movie
			}
			else
				return 0.0;
		}
		else{
			//Count the already rated items
			int alreadyRated = 0;
			for (T item : nItem.getItems())
				if (hasRatedItem(agent, item))
					alreadyRated++;

			double alreadyRatedPercentage = ((double)alreadyRated)/nItem.getItems().size();
			if (alreadyRatedPercentage > flexibilityLevel){ //if the amount of already rated items exceeds the flexibility (ex: 80% of the items of the package were rated by the user, and the flexibility value is 60% (0.6) => apply punishment
				double punishment = 1-flexibilityLevel;
				return  originalUtilityValue * punishment; //is negative because this modifier reduces de utility if the user has already rated the movie
			}
			else
				return 0.0;
		}
	}

	@Override
	public String toString() {
		return "ARPStrategyFlexible [flexibilityLevel=" + flexibilityLevel + "]";
	}

}
