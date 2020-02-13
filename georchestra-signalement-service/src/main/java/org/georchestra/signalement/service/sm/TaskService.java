/**
 * 
 */
package org.georchestra.signalement.service.sm;

import java.util.List;
import java.util.UUID;

import org.georchestra.signalement.core.common.DocumentContent;
import org.georchestra.signalement.core.dto.ReportingDescription;
import org.georchestra.signalement.core.dto.Task;

/**
 * @author FNI18300
 *
 */
public interface TaskService {

	Task claimTask(String taskId);

	Task createDraft(ReportingDescription reportingDescription);

	void doIt(String taskId, String actionName);

	List<Task> searchTasks();

	Task startTask(Task task);

	void addAttachment(UUID reportingUuid, DocumentContent content);

	DocumentContent getAttachment(UUID reportingUuid, String attachmentId);
}
