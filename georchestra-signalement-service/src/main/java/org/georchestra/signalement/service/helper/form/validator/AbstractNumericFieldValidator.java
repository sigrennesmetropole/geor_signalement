/**
 * 
 */
package org.georchestra.signalement.service.helper.form.validator;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.georchestra.signalement.core.dto.Field;
import org.georchestra.signalement.core.dto.FieldType;

import lombok.extern.slf4j.Slf4j;

/**
 * @author FNI18300
 *
 */
@Slf4j
public abstract class AbstractNumericFieldValidator extends AbstractFieldValidator {

	@Override
	public boolean check(Field field) {
		boolean result = true;
		if (CollectionUtils.isNotEmpty(field.getValues())) {
			for (String value : field.getValues()) {
				if (StringUtils.isNotEmpty(value)) {
					try {
						result &= check(field, value);
					} catch (Exception e) {
						log.error("Invalid configuration for " + field.getDefinition());
					}
				}
			}
		}
		return result;
	}

	protected boolean check(Field field, String value) {
		boolean result = true;
		var definition = field.getDefinition();
		if (definition != null && definition.getType() == FieldType.DOUBLE) {
			double d = Double.parseDouble(value);
			if (innerCheck(d)) {
				result = false;
			}
		} else if (definition != null && definition.getType() == FieldType.LONG) {
			long d = Long.parseLong(value);
			if (innerCheck(d)) {
				result = false;
			}
		}
		return result;
	}

	protected abstract boolean innerCheck(double d);

	protected abstract boolean innerCheck(long d);

	@Override
	public boolean accept(Field field) {
		var definition = field.getDefinition();
		return definition != null && (definition.getType() == FieldType.DOUBLE
				|| definition.getType() == FieldType.LONG) && innerAccept(field);
	}

	protected abstract boolean innerAccept(Field field);

}
