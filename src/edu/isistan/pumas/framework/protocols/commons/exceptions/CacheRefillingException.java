package edu.isistan.pumas.framework.protocols.commons.exceptions;

public class CacheRefillingException extends Exception {

	private static final long serialVersionUID = -4491903445506585923L;

	protected static String customMsg = "It's not possible to refill the ranking cache. There are no more items."; 
	
	public CacheRefillingException(){
		super(customMsg);
	}
	
	public CacheRefillingException (Throwable cause){
		super(customMsg, cause);
	}
}
