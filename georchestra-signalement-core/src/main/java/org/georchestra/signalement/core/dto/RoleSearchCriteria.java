/**
 * 
 */
package org.georchestra.signalement.core.dto;

import java.util.List;

import lombok.Data;

/**
 * @author FNI18300
 *
 */
@Data
public class RoleSearchCriteria {

	private List<Long> contextDescriptionIds;

	private List<String> contextDescriptionNames;

	private List<String> userNames;
}
