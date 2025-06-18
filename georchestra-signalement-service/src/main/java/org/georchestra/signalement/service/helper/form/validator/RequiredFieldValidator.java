/**
 * 
 */
package org.georchestra.signalement.service.helper.form.validator;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.georchestra.signalement.core.dto.Field;
import org.georchestra.signalement.core.dto.ValidatorType;
import org.springframework.stereotype.Component;

/**
 * @author FNI18300
 *
 */
@Component
public class RequiredFieldValidator extends AbstractFieldValidator {

	@Override
	public boolean check(Field field) {
		boolean result = false;
		if (CollectionUtils.isNotEmpty(field.getValues())) {
			for (String value : field.getValues()) {
				if (StringUtils.isNotEmpty(value)) {
					result = true;
					break;
				}
			}
		}
		return result;
	}

	@Override
	public boolean accept(Field field) {
		return lookValidator(field, ValidatorType.REQUIRED) != null;
	}

}
