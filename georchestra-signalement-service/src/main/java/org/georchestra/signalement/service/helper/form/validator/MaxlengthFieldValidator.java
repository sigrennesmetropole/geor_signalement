/**
 * 
 */
package org.georchestra.signalement.service.helper.form.validator;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.georchestra.signalement.core.dto.Field;
import org.georchestra.signalement.core.dto.FieldType;
import org.georchestra.signalement.core.dto.Validator;
import org.georchestra.signalement.core.dto.ValidatorType;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author FNI18300
 *
 */
@Slf4j
@Component
public class MaxlengthFieldValidator extends AbstractFieldValidator {

	@Override
	public boolean check(Field field) {
		boolean result = true;
		Validator validator = lookValidator(field, ValidatorType.MAXLENGTH);
		try {
			int maxlength = Integer.parseInt(validator.getAttribute());
			if (CollectionUtils.isNotEmpty(field.getValues())) {
				for (String value : field.getValues()) {
					if (StringUtils.isNotEmpty(value) && value.length() > maxlength) {
						result = false;
						break;
					}
				}
			}
		} catch (Exception e) {
			log.error("Invalid configuration for " + field.getDefinition());
		}
		return result;
	}

	@Override
	public boolean accept(Field field) {
		return (field.getDefinition().getType() == FieldType.STRING)
				&& lookValidator(field, ValidatorType.MAXLENGTH) != null;
	}

}
