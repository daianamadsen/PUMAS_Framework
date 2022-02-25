package edu.isistan.pumas.framework.protocols.commons.exceptions;

public class NothingToProposeException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7817065276316925804L;
	protected static String customMsg = "The agent has nothing more to propose."; 
	
	public NothingToProposeException(){
		super(customMsg);
	}
	
	public NothingToProposeException (Throwable cause){
		super(customMsg, cause);
	}
	
}
