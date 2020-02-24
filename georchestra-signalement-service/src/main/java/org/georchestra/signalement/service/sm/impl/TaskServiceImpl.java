/**
 * 
 */
package org.georchestra.signalement.service.sm.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.collections4.CollectionUtils;
import org.georchestra.signalement.core.common.DocumentContent;
import org.georchestra.signalement.core.dao.acl.ContextDescriptionDao;
import org.georchestra.signalement.core.dao.reporting.ReportingDao;
import org.georchestra.signalement.core.dto.Attachment;
import org.georchestra.signalement.core.dto.ReportingDescription;
import org.georchestra.signalement.core.dto.Status;
import org.georchestra.signalement.core.dto.Task;
import org.georchestra.signalement.core.entity.acl.ContextDescriptionEntity;
import org.georchestra.signalement.core.entity.reporting.AbstractReportingEntity;
import org.georchestra.signalement.core.entity.reporting.LineReportingEntity;
import org.georchestra.signalement.service.exception.DocumentRepositoryException;
import org.georchestra.signalement.service.helper.authentification.AuthentificationHelper;
import org.georchestra.signalement.service.helper.reporting.ReportingHelper;
import org.georchestra.signalement.service.mapper.ReportingMapper;
import org.georchestra.signalement.service.sm.TaskService;
import org.georchestra.signalement.service.st.repository.DocumentRepositoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author FNI18300
 *
 */
@Component
public class TaskServiceImpl implements TaskService {

	private static final Logger LOGGER = LoggerFactory.getLogger(TaskServiceImpl.class);

	@Value("${attachment.max-count:5}")
	private int attachmentMaxCount;

	@Autowired
	private ProcessEngine processEngine;

	@Autowired
	private ReportingDao reportingDao;

	@Autowired
	private DocumentRepositoryService documentRepositoryService;

	@Autowired
	private ContextDescriptionDao contextDescriptionDao;

	@Autowired
	private ReportingHelper reportingHelper;

	@Autowired
	private AuthentificationHelper authentificationHelper;

	@Autowired
	private ReportingMapper reportingMapper;

	private Map<UUID, AbstractReportingEntity> DRAFT_REPORTING_ENTITIES = new HashMap<>();

	@Override
	public Task createDraft(ReportingDescription reportingDescription) {
		if (reportingDescription == null || reportingDescription.getContextDescription() == null) {
			throw new IllegalArgumentException("Reporting with a context is mandatory");
		}
		ContextDescriptionEntity contextDescription = contextDescriptionDao
				.findByName(reportingDescription.getContextDescription().getName());
		if (contextDescription == null) {
			throw new IllegalArgumentException(
					"Invalid context name:" + reportingDescription.getContextDescription().getName());
		}
		AbstractReportingEntity reportingEntity = reportingHelper.createReportingEntity(contextDescription,
				authentificationHelper.getUsername());
		DRAFT_REPORTING_ENTITIES.put(reportingEntity.getUuid(), reportingEntity);

		return reportingHelper.createTaskFromReporting(reportingMapper.entityToDto(reportingEntity));
	}

	@Override
	public Task startTask(Task task) {
		if (task == null || task.getAsset() == null) {
			throw new IllegalArgumentException("Task with asset is mandatory");
		}
		AbstractReportingEntity reportingEntity = DRAFT_REPORTING_ENTITIES.get(task.getAsset().getUuid());
		if (reportingEntity == null) {
			throw new IllegalArgumentException("Invalid task");
		}
		// TODO set geometry ou update data
		reportingEntity.setUpdatedDate(new Date());
		reportingDao.save(reportingEntity);

		Map<String, Object> variables = fillProcessVariables(null);
		ProcessInstance processInstance = startProcessInstance(reportingEntity, variables);

		Task outputTask = reportingHelper.createTaskFromReporting(reportingMapper.entityToDto(reportingEntity));
		outputTask.setId(processInstance.getId());

		DRAFT_REPORTING_ENTITIES.remove(reportingEntity.getUuid());
		return outputTask;
	}

	private Map<String, Object> fillProcessVariables(Map<String, Object> variables) {
		if (variables == null) {
			variables = new HashMap<String, Object>();
		}
		variables.put("employeeName", "Kermit");
		variables.put("numberOfDays", new Integer(4));
		variables.put("vacationMotivation", "I'm really tired!");
		return variables;
	}

	private ProcessInstance startProcessInstance(AbstractReportingEntity reportingEntity,
			Map<String, Object> variables) {
		String processDefinitionId = lookupProcessInstanceId(reportingEntity.getContextDescription());
		RuntimeService runtimeService = processEngine.getRuntimeService();
		ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinitionId,
				reportingEntity.getId().toString(), variables);
		return processInstance;
	}

	private String lookupProcessInstanceId(ContextDescriptionEntity contextDescription) {
		String result = null;
		RepositoryService repositoryService = processEngine.getRepositoryService();

		ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery().active()
				.processDefinitionKey(contextDescription.getProcessDefinitionKey());
		if (contextDescription.getRevision() != null) {
			processDefinitionQuery.processDefinitionVersion(contextDescription.getRevision());
		} else {
			processDefinitionQuery.latestVersion();
		}
		List<ProcessDefinition> processDefinitions = processDefinitionQuery.list();
		if (CollectionUtils.isNotEmpty(processDefinitions)) {
			result = processDefinitions.get(0).getId();
		}
		return result;
	}

	@Override
	public Task claimTask(String taskId) {
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
	public Attachment addAttachment(UUID reportingUuid, DocumentContent documentContent)
			throws DocumentRepositoryException {
		Attachment result = null;
		AbstractReportingEntity reportingEntity = DRAFT_REPORTING_ENTITIES.get(reportingUuid);
		if (reportingEntity == null) {
			reportingEntity = reportingDao.findByUuid(reportingUuid);
		}
		if (reportingEntity == null) {
			throw new IllegalArgumentException("Invalid reporting Uuid");
		}
		List<Long> attachmentIds = documentRepositoryService.getDocumentIds(reportingUuid.toString());
		if (attachmentIds != null && attachmentIds.size() > attachmentMaxCount) {
			throw new IllegalArgumentException("Maximum attachments per reporting is reached");
		}

		Long documentId = documentRepositoryService.createDocument(Arrays.asList(reportingUuid.toString()),
				documentContent);
		result = new Attachment();
		result.setMimeType(documentContent.getContentType());
		result.setName(documentContent.getFileName());
		result.setId(documentId);
		return result;
	}

	@Override
	public DocumentContent getAttachment(UUID reportingUuid, Long attachmentId) throws DocumentRepositoryException {
		DocumentContent result = null;
		AbstractReportingEntity reportingEntity = DRAFT_REPORTING_ENTITIES.get(reportingUuid);
		if (reportingEntity == null) {
			reportingEntity = reportingDao.findByUuid(reportingUuid);
		}
		if (reportingEntity == null) {
			throw new IllegalArgumentException("Invalid reporting Uuid");
		}
		List<Long> documentIds = documentRepositoryService.getDocumentIds(reportingUuid.toString());
		if (documentIds != null && documentIds.contains(attachmentId)) {
			result = documentRepositoryService.getDocument(attachmentId);
		}

		return result;
	}

	@Override
	public void removeAttachment(UUID reportingUuid, Long attachmentId) throws DocumentRepositoryException {
		AbstractReportingEntity reportingEntity = DRAFT_REPORTING_ENTITIES.get(reportingUuid);
		if (reportingEntity == null) {
			reportingEntity = reportingDao.findByUuid(reportingUuid);
		}
		if (reportingEntity == null) {
			throw new IllegalArgumentException("Invalid reporting Uuid");
		}
		List<Long> documentIds = documentRepositoryService.getDocumentIds(reportingUuid.toString());
		if (documentIds != null && documentIds.contains(attachmentId)) {
			documentRepositoryService.deleteDocument(attachmentId);
		}
	}

	@Override
	public void cancelDraft(UUID reportingUuid) {
		AbstractReportingEntity reportingEntity = DRAFT_REPORTING_ENTITIES.get(reportingUuid);
		if (reportingEntity == null) {
			reportingEntity = reportingDao.findByUuid(reportingUuid);
		}
		if (reportingEntity == null) {
			throw new IllegalArgumentException("Invalid reporting Uuid");
		}
		if (Status.DRAFT != reportingEntity.getStatus()) {
			throw new IllegalArgumentException("Invalid reporting status:" + reportingEntity.getStatus());
		}
		try {
			if (reportingEntity.getId() == null) {
				DRAFT_REPORTING_ENTITIES.remove(reportingUuid);
			} else {
				reportingDao.delete(reportingEntity);
			}
			documentRepositoryService.deleteDocuments(reportingUuid.toString());
		} catch (Exception e) {
			LOGGER.warn("Failed to delete attachments", e);
		}
	}

}
