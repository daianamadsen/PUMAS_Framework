package edu.isistan.pumas.framework.protocols.commons.exceptions;

public class NonConcedableCurrentProposalException extends Exception {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1486863171807685376L;
	
	protected static String customMsg = "The agent has ran out of proposes. He doesn't have nothing more to propose because either "
			+ "the list of candidate proposals was empty or all the proposals weren't better than the current one"
			+ " (from the multilateral concession strategy point of view)"; 
	
	public NonConcedableCurrentProposalException(){
		super(customMsg);
	}
	
	public NonConcedableCurrentProposalException (Throwable cause){
		super(customMsg, cause);
	}
}
