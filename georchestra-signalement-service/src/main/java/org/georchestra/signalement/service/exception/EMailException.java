/**
 * 
 */
package org.georchestra.signalement.service.exception;

/**
 * @author FNI18300
 *
 */
public class EMailException extends Exception {

	private static final long serialVersionUID = -6313510009433868085L;

	/**
	 * 
	 */
	public EMailException() {
	}

	/**
	 * @param message
	 */
	public EMailException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public EMailException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public EMailException(String message, Throwable cause) {
		super(message, cause);
	}

}
