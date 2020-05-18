/**
 * 
 */
package org.georchestra.signalement.service.exception;

/**
 * @author FNI18300
 *
 */
public class DocumentGenerationException extends Exception {

	private static final long serialVersionUID = 4432149817539372711L;

	/**
	 * 
	 */
	public DocumentGenerationException() {
	}

	/**
	 * @param message
	 */
	public DocumentGenerationException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public DocumentGenerationException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DocumentGenerationException(String message, Throwable cause) {
		super(message, cause);
	}

}
