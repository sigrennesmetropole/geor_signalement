/**
 * 
 */
package org.georchestra.signalement.service.helper.form.validator;

import org.apache.commons.collections4.CollectionUtils;
import org.georchestra.signalement.core.dto.Field;
import org.georchestra.signalement.core.dto.Validator;
import org.georchestra.signalement.core.dto.ValidatorType;

/**
 * @author FNI18300
 *
 */
public abstract class AbstractFieldValidator implements FieldValidator {

	protected Validator lookValidator(Field field, ValidatorType validatorType) {
		Validator result = null;
		if (CollectionUtils.isNotEmpty(field.getDefinition().getValidators())) {
			result = field.getDefinition().getValidators().stream().filter(v -> v.getType() == validatorType)
					.findFirst().orElse(null);
		}
		return result;
	}

}
