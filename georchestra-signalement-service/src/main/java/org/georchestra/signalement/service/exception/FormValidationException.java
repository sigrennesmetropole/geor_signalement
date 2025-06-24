/**
 * 
 */
package org.georchestra.signalement.service.exception;

/**
 * @author FNI18300
 *
 */
public class FormValidationException extends Exception {

	private static final long serialVersionUID = -6313510009433868085L;

	/**
	 * 
	 */
	public FormValidationException() {
	}

	/**
	 * @param message
	 */
	public FormValidationException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public FormValidationException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public FormValidationException(String message, Throwable cause) {
		super(message, cause);
	}

}
