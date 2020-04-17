/**
 * 
 */
package org.georchestra.signalement.service.exception;

/**
 * @author FNI18300
 *
 */
public class DataException extends Exception {

	private static final long serialVersionUID = -6313510009433868085L;

	/**
	 * 
	 */
	public DataException() {
	}

	/**
	 * @param message
	 */
	public DataException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public DataException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DataException(String message, Throwable cause) {
		super(message, cause);
	}

}
