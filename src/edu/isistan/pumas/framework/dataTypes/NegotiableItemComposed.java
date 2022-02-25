package edu.isistan.pumas.framework.dataTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import edu.isistan.christian.recommenders.sur.datatypes.SURItem;

public class NegotiableItemComposed<T extends SURItem> implements NegotiableItem<T> {

	Set<T> innerItems;

	public NegotiableItemComposed(Set<T> innerItems){
		this.innerItems = innerItems;
	}
	
	public List<T> getItems(){
		return new ArrayList<T>(innerItems);
	}
	
	@Override
	public String toString() {
		return "NegotiableItemComposed [innerItems=" + innerItems + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((innerItems == null) ? 0 : innerItems.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NegotiableItemComposed<?> other = (NegotiableItemComposed<?>) obj;
		if (innerItems == null) {
			if (other.innerItems != null)
				return false;
		} else if (!innerItems.equals(other.innerItems))
			return false;
		return true;
	}
	
	
}
