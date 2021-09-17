package org.georchestra.signalement.api.handler;

import org.georchestra.signalement.core.dto.ApiError;
import org.georchestra.signalement.service.exception.ApiServiceException;
import org.georchestra.signalement.service.exception.FormDefinitionException;
import org.georchestra.signalement.service.exception.InvalidDataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *
 */
@ControllerAdvice
public class ApiExceptionHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(ApiExceptionHandler.class);

	/**
	 * Constructor
	 */
	public ApiExceptionHandler() {
		super();
	}

	/**
	 * Capture les exceptions de type <code>AppServiceException.class</code> pour la
	 * transformer en erreur json
	 *
	 * @param e Exception capturée
	 * @return Code erreur json
	 */
	@ExceptionHandler(ApiServiceException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public @ResponseBody
	ApiError handleApiServiceException(final ApiServiceException e) {
		LOGGER.info("AppServiceException", e);
		final ApiError error = new ApiError();
		error.setCode(e.getAppExceptionStatusCode());
		error.setLabel(e.getLocalizedMessage());
		return error;
	}

	/**
	 * Capture les exceptions de type <code>InvalidDataException.class</code>
	 * pour la transformer en erreur json
	 * Erreurs levées lorsque les données envoyées sont contraires aux contraintes métiers
	 * <p>
	 * Capture les exceptions de type <code>IllegalArgumentException.class</code>
	 * pour la transformer en erreur json
	 * Erreurs levées lorsque les données envoyées sont incorrectes
	 * <p>
	 * Capture les exceptions de type <code>FormDefinitionException.class</code>
	 * pour la transformer en erreur json
	 *
	 * @param e Exception capturée
	 * @return Code erreur json
	 */
	@ExceptionHandler({InvalidDataException.class, FormDefinitionException.class, IllegalArgumentException.class})
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public @ResponseBody
	ApiError handleBadRequestException(final Exception e) {
		final ApiError error = new ApiError();
		error.setCode(e.getLocalizedMessage());
		return error;
	}

}
