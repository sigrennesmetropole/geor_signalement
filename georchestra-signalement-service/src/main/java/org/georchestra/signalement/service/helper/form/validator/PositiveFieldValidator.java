/**
 * 
 */
package org.georchestra.signalement.service.helper.form.validator;

import org.georchestra.signalement.core.dto.Field;
import org.georchestra.signalement.core.dto.ValidatorType;
import org.springframework.stereotype.Component;

/**
 * @author FNI18300
 *
 */
@Component
public class PositiveFieldValidator extends AbstractNumericFieldValidator {

	@Override
	protected boolean innerCheck(double d) {
		return d < 0;
	}

	@Override
	protected boolean innerCheck(long d) {
		return d < 0;
	}

	@Override
	protected boolean innerAccept(Field field) {
		return lookValidator(field, ValidatorType.POSITIVE) != null;
	}

}
