/**
 * 
 */
package org.georchestra.signalement.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fni18300
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSearchCriteria {

	private String login;
	
	private String email;
	
}
