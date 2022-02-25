package edu.isistan.pumas.framework.protocols.commons.userAgents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.isistan.christian.recommenders.sur.datatypes.SURItem;
import edu.isistan.christian.recommenders.sur.datatypes.SURUser;

public class UserAgContainer<T extends SURItem> extends HashMap<String, UserAg<T>>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5181839261164060528L;

	public UserAgContainer() {
		super();
	}
	/**
	 * Returns a list of the agents ({@link UserAg}) in the container
	 */
	@Override
	public List<UserAg<T>> values(){
		return new ArrayList<> (super.values());
	}
	
	public UserAg<T> searchBy (SURUser representedUser){
		UserAg<T> ag = null;
		
		List<UserAg<T>> allAgents = new ArrayList<> (this.values()); 
		boolean found = false;
		for (int i=0; i<allAgents.size() && !found; i++){
			found = allAgents.get(i).getRepresentedUser().equals(representedUser);
			if (found)
				ag = allAgents.get(i);
		}
		
		return ag;
	}
}
