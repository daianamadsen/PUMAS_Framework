package edu.isistan.pumas.framework.protocols.commons.exceptions;

public class NoProposalsAvailableException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1167539371635319475L;

	protected static String customMsg = "The agent has ran out of proposes. He doesn't have nothing more to propose because either "
			+ "the list of candidate proposals was empty or all the proposals weren't better than the current one"
			+ " (from the multilateral concession strategy point of view) or the agent hasn't proposed anything at the begining of the negotiation"; 
	
	public NoProposalsAvailableException(){
		super(customMsg);
	}
	
	public NoProposalsAvailableException (Throwable cause){
		super(customMsg, cause);
	}
	
}
