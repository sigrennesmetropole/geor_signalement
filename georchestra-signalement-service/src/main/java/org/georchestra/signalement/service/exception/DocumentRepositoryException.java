/**
 * 
 */
package org.georchestra.signalement.service.exception;

/**
 * @author FNI18300
 *
 */
public class DocumentRepositoryException extends Exception {

	private static final long serialVersionUID = -6313510009433868085L;

	/**
	 * 
	 */
	public DocumentRepositoryException() {
	}

	/**
	 * @param message
	 */
	public DocumentRepositoryException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public DocumentRepositoryException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DocumentRepositoryException(String message, Throwable cause) {
		super(message, cause);
	}

}
