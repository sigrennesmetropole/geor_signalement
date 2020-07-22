/**
 * 
 */
package org.georchestra.signalement.service.dto;

import org.georchestra.signalement.core.dto.ContextType;
import org.georchestra.signalement.core.dto.GeographicType;

import lombok.Data;

/**
 * @author FNI18300
 *
 */
@Data
public class TaskSearchCriteria {

	private String contextName;

	private ContextType contextType;

	private GeographicType geographicType;
	
	private boolean asAdmin;

}
