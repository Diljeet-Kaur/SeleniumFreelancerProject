package com.test.mvn.proj.exception;

/**
 * The Class EmergartSeleniumException.
 */
public class EmergartSeleniumException extends Exception {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1484672123866128618L;

	/**
	 * Instantiates a new emergart selenium exception.
	 */
	public EmergartSeleniumException() {
		super();
	}
	
	/**
	 * Instantiates a new emergart selenium exception.
	 *
	 * @param message the message
	 */
	public EmergartSeleniumException(String message){
		super(message);
	}
	
	/**
	 * Instantiates a new emergart selenium exception.
	 *
	 * @param cause the cause
	 */
	public EmergartSeleniumException(Throwable cause){
		super(cause);
	}
	
	/**
	 * Instantiates a new emergart selenium exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public EmergartSeleniumException(String message, Throwable cause){
		super(message, cause);
	}
}
