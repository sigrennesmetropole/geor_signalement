/**
 * 
 */
package org.georchestra.signalement.core.dto;

import lombok.Data;

/**
 * @author FNI18300
 */
@Data
public class ContextDescriptionSearchCriteria {

	private ContextType contextType;

	private GeographicType geographicType;

	private String description;

	private String processDefinitionKey;

}
