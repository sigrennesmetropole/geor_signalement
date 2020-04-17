/**
 * 
 */
package org.georchestra.signalement.service.exception;

/**
 * @author FNI18300
 *
 */
public class DocumentModelNotFoundException extends Exception {

	private static final long serialVersionUID = 8628200214346034954L;

	/**
	 * 
	 */
	public DocumentModelNotFoundException() {
	}

	/**
	 * @param message
	 */
	public DocumentModelNotFoundException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public DocumentModelNotFoundException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DocumentModelNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
