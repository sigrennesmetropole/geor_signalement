/**
 * 
 */
package org.georchestra.signalement.core.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import org.apache.commons.lang3.StringUtils;

/**
 * @author FNI18300
 *
 */
@Converter
public class ListToStringConverter implements AttributeConverter<List<String>, String> {

	private static final String SEPARATOR = ",";

	@Override
	public String convertToDatabaseColumn(List<String> attribute) {
		if (attribute == null || attribute.isEmpty()) {
			return "";
		}
		return StringUtils.join(attribute, SEPARATOR);
	}

	@Override
	public List<String> convertToEntityAttribute(String dbData) {
		if (dbData == null || dbData.trim().length() == 0) {
			return new ArrayList<>();
		}

		String[] data = dbData.split(SEPARATOR);
		// attention pas d'utilisation de Arrays.asList car sinon on a une liste non modifiable
		List<String> result = new ArrayList<>();
		if( data != null) {
			result.addAll(Arrays.asList(data));
		}
		return result;
	}
}
