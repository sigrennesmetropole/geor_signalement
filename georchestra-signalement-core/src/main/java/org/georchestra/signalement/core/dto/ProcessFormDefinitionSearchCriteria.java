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
public class ProcessFormDefinitionSearchCriteria {

	private String processDefinitionId;

	private Integer revision;

	private boolean acceptFlexRevision = false;

	private String userTaskId;

	private boolean acceptFlexUserTaskId = false;

	public ProcessFormDefinitionSearchCriteria(String processDefinitionId, Integer revision, boolean acceptFlexRevision,
			String userTaskId, boolean acceptFlexUserTaskId) {
		super();
		this.processDefinitionId = processDefinitionId;
		this.revision = revision;
		this.acceptFlexRevision = acceptFlexRevision;
		this.userTaskId = userTaskId;
		this.acceptFlexUserTaskId = acceptFlexUserTaskId;
	}

	public ProcessFormDefinitionSearchCriteria() {
	}
}
