/**
 * 
 */
package org.georchestra.signalement.service.sm;

import java.util.List;
import java.util.UUID;

import org.georchestra.signalement.core.common.DocumentContent;
import org.georchestra.signalement.core.dto.Attachment;
import org.georchestra.signalement.core.dto.AttachmentConfiguration;
import org.georchestra.signalement.core.dto.Form;
import org.georchestra.signalement.core.dto.ReportingDescription;
import org.georchestra.signalement.core.dto.Task;
import org.georchestra.signalement.service.exception.DataException;
import org.georchestra.signalement.service.exception.DocumentRepositoryException;
import org.georchestra.signalement.service.exception.FormConvertException;
import org.georchestra.signalement.service.exception.FormDefinitionException;

/**
 * @author FNI18300
 *
 */
public interface TaskService {

	Task claimTask(String taskId);

	Task createDraft(ReportingDescription reportingDescription);

	Form lookupDrafForm(String contextDescriptionName) throws FormDefinitionException;

	void cancelDraft(UUID reportingUuid);

	void doIt(String taskId, String actionName) throws DataException;

	List<Task> searchTasks();

	Task getTask(String taskId);

	Task updateTask(Task task) throws DataException, FormDefinitionException, FormConvertException;

	Task startTask(Task task)
			throws DocumentRepositoryException, DataException, FormDefinitionException, FormConvertException;

	Attachment addAttachment(UUID reportingUuid, DocumentContent content) throws DocumentRepositoryException;

	DocumentContent getAttachment(UUID reportingUuid, Long attachmentId) throws DocumentRepositoryException;

	void removeAttachment(UUID reportingUuid, Long attachmentId) throws DocumentRepositoryException;

	AttachmentConfiguration getAttachmentConfiguration();
}
