/**
 * 
 */
package org.georchestra.signalement.core.dto;

import org.georchestra.signalement.core.dto.ContextType;
import org.georchestra.signalement.core.dto.GeographicType;

import lombok.Data;

/**
 * @author FNI18300
 *
 */
@Data
public class ContextDescriptionSearchCriteria {

	private ContextType contextType;

	private GeographicType geographicType;

}
