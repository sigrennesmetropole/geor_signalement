/**
 * 
 */
package org.georchestra.signalement.core.dto;

import lombok.Data;

/**
 * @author FNI18300
 *
 */
@Data
public class ProcessStylingSearchCriteria {

	private String processDefinitionId;

	private Integer revision;

	private String userTaskId;

}
