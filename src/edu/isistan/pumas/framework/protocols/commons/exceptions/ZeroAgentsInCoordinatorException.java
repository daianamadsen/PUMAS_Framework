package edu.isistan.pumas.framework.protocols.commons.exceptions;

public class ZeroAgentsInCoordinatorException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5212477177633532751L;
	protected static String customMsg = "There are zero agents inside the coordinator. Therefore, the protocol can't be executed."; 
	
	public ZeroAgentsInCoordinatorException(){
		super(customMsg);
	}
	
}
