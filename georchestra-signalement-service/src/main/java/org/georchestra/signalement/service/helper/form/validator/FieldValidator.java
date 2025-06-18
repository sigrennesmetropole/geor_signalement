/**
 * 
 */
package org.georchestra.signalement.service.helper.form.validator;

import org.georchestra.signalement.core.dto.Field;

/**
 * @author FNI18300
 *
 */
public interface FieldValidator {

	boolean accept(Field field);

	boolean check(Field field);
}
