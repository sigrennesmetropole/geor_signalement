/**
 * 
 */
package org.georchestra.signalement.service.sm.impl;

import java.util.List;
import java.util.UUID;

import org.georchestra.signalement.core.common.DocumentContent;
import org.georchestra.signalement.core.dto.ReportingDescription;
import org.georchestra.signalement.core.dto.Task;
import org.georchestra.signalement.service.sm.TaskService;
import org.springframework.stereotype.Component;

/**
 * @author FNI18300
 *
 */
@Component
public class TaskServceImpl implements TaskService {


	@Override
	public Task claimTask(String taskId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Task createDraft(ReportingDescription reportingDescription) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void doIt(String taskId, String actionName) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Task> searchTasks() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Task startTask(Task task) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addAttachment(UUID reportingUuid, DocumentContent content) {
		// TODO Auto-generated method stub

	}

	@Override
	public DocumentContent getAttachment(UUID reportingUuid, String attachmentId) {
		// TODO Auto-generated method stub
		return null;
	}

}
