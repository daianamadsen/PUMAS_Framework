package edu.isistan.pumas.framework.dataTypes;


import java.util.List;

import edu.isistan.christian.recommenders.sur.datatypes.SURItem;

public interface NegotiableItem<T extends SURItem> {
	
	public List<T> getItems();
	
	public int hashCode();
	
	public boolean equals(Object obj);
	
	public String toString();

}
