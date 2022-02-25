package edu.isistan.pumas.framework.dataTypes;

import java.util.ArrayList;
import java.util.List;

import edu.isistan.christian.recommenders.sur.datatypes.SURItem;

public class NegotiableItemSimple<T extends SURItem> implements NegotiableItem<T> {

	T innerItem;
	
	public NegotiableItemSimple(T innerItem){
		this.innerItem = innerItem;
	}
	
	public List<T> getItems(){
		List<T> items = new ArrayList<>();
		items.add(this.innerItem);
		return items;
	}
	
	@Override
	public String toString() {
		return "NegotiableItemSimple [innerItem=" + innerItem + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((innerItem == null) ? 0 : innerItem.hashCode());
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
		NegotiableItemSimple<?> other = (NegotiableItemSimple<?>) obj;
		if (innerItem == null) {
			if (other.innerItem != null)
				return false;
		} else if (!innerItem.equals(other.innerItem))
			return false;
		return true;
	}
	
	
}
