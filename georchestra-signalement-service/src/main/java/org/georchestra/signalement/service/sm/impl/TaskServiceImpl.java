/**
 * 
 */
package org.georchestra.signalement.service.sm.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.georchestra.signalement.core.common.DocumentContent;
import org.georchestra.signalement.core.dao.acl.ContextDescriptionDao;
import org.georchestra.signalement.core.dao.reporting.ReportingDao;
import org.georchestra.signalement.core.dto.Attachment;
import org.georchestra.signalement.core.dto.AttachmentConfiguration;
import org.georchestra.signalement.core.dto.Form;
import org.georchestra.signalement.core.dto.ReportingDescription;
import org.georchestra.signalement.core.dto.Status;
import org.georchestra.signalement.core.dto.Task;
import org.georchestra.signalement.core.entity.acl.ContextDescriptionEntity;
import org.georchestra.signalement.core.entity.reporting.AbstractReportingEntity;
import org.georchestra.signalement.service.exception.DataException;
import org.georchestra.signalement.service.exception.DocumentRepositoryException;
import org.georchestra.signalement.service.exception.FormConvertException;
import org.georchestra.signalement.service.exception.FormDefinitionException;
import org.georchestra.signalement.service.helper.authentification.AuthentificationHelper;
import org.georchestra.signalement.service.helper.form.FormHelper;
import org.georchestra.signalement.service.helper.reporting.AttachmentHelper;
import org.georchestra.signalement.service.helper.reporting.ReportingHelper;
import org.georchestra.signalement.service.helper.workflow.BpmnHelper;
import org.georchestra.signalement.service.mapper.reporting.ReportingMapper;
import org.georchestra.signalement.service.sm.TaskService;
import org.georchestra.signalement.service.st.repository.DocumentRepositoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author FNI18300
 *
 */
@Component
@Transactional(readOnly = true)
public class TaskServiceImpl implements TaskService {

	private static final String INVALID_REPORTING_UUID_MESSAGE = "Invalid reporting Uuid";

	private static final Logger LOGGER = LoggerFactory.getLogger(TaskServiceImpl.class);

	private static final String ACTION_VARIABLE_NAME = "action";

	private static Map<UUID, AbstractReportingEntity> DRAFT_REPORTING_ENTITIES = new HashMap<>();

	private static Map<UUID, List<DocumentContent>> DRAFT_ATTACHMENTS_ENTITIES = new HashMap<>();

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
	private FormHelper formHelper;

	@Autowired
	private BpmnHelper bpmnHelper;

	@Autowired
	private AttachmentHelper attachmentHelper;

	@Autowired
	private ReportingMapper reportingMapper;

	@Override
	public Form lookupDrafForm(String contextDescriptionName) throws FormDefinitionException {
		return formHelper.lookupDraftForm(contextDescriptionName);
	}

	@Override
	public Task createDraft(ReportingDescription reportingDescription) {
		// contrôle des paramètres
		if (reportingDescription == null || reportingDescription.getContextDescription() == null) {
			throw new IllegalArgumentException("Reporting with a context is mandatory");
		}

		// récupération du context
		ContextDescriptionEntity contextDescription = contextDescriptionDao
				.findByName(reportingDescription.getContextDescription().getName());
		if (contextDescription == null) {
			throw new IllegalArgumentException(
					"Invalid context name:" + reportingDescription.getContextDescription().getName());
		}
		// création de l'entité
		AbstractReportingEntity reportingEntity = reportingHelper.createReportingEntity(contextDescription,
				authentificationHelper.getUsername());

		// TODO set geometry

		// mise à jour de l'entité
		reportingMapper.updateEntityFromDto(reportingDescription, reportingEntity);

		// mise en cache
		DRAFT_REPORTING_ENTITIES.put(reportingEntity.getUuid(), reportingEntity);

		return reportingHelper.createTaskFromReporting(reportingMapper.entityToDto(reportingEntity));
	}

	@Override
	@Transactional(readOnly = false)
	public Task startTask(Task task)
			throws DocumentRepositoryException, DataException, FormDefinitionException, FormConvertException {
		// contrôle des données d'entrée
		if (task == null || task.getAsset() == null) {
			throw new IllegalArgumentException("Task with asset is mandatory");
		}
		// récupération du signalement draft
		AbstractReportingEntity reportingEntity = DRAFT_REPORTING_ENTITIES.get(task.getAsset().getUuid());
		if (reportingEntity == null) {
			throw new IllegalArgumentException("Invalid task");
		}
		// mise à jour de l'entité
		task = updateDraftTask(task, reportingEntity);

		// l'entité peut avoir changer en cas de cnahgement de contexte
		reportingEntity = DRAFT_REPORTING_ENTITIES.get(task.getAsset().getUuid());
		reportingDao.save(reportingEntity);

		// ajout des attachments
		List<DocumentContent> attachments = DRAFT_ATTACHMENTS_ENTITIES.get(reportingEntity.getUuid());
		if (CollectionUtils.isNotEmpty(attachments)) {
			for (DocumentContent attachment : attachments) {
				addRunningAttachment(reportingEntity.getUuid(), attachment);
			}
		}

		// Démarrage du processus
		Map<String, Object> variables = fillProcessVariables(reportingEntity, null);
		ProcessInstance processInstance = startProcessInstance(reportingEntity, variables);

		// conversion
		Task outputTask = reportingHelper.createTaskFromReporting(reportingMapper.entityToDto(reportingEntity));
		outputTask.setId(processInstance.getId());

		// nettoyage
		DRAFT_REPORTING_ENTITIES.remove(reportingEntity.getUuid());
		DRAFT_ATTACHMENTS_ENTITIES.remove(reportingEntity.getUuid());
		return outputTask;
	}

	private Map<String, Object> fillProcessVariables(AbstractReportingEntity reportingEntity,
			Map<String, Object> variables) throws DataException {
		if (variables == null) {
			variables = new HashMap<>();
		}
		if (reportingEntity != null) {
			variables.put("meId", reportingEntity.getId());
			variables.put("meUuid", reportingEntity.getUuid());
			Map<String, Object> datas = reportingHelper.hydrateData(reportingEntity.getDatas());
			if (MapUtils.isNotEmpty(datas)) {
				for (Map.Entry<String, Object> data : datas.entrySet()) {
					variables.put(data.getKey(), data.getValue());
				}
			}
		}
		return variables;
	}

	@Override
	@Transactional(readOnly = false)
	public Task claimTask(String taskId) {
		Task result = null;
		LOGGER.debug("Claim on task {}", taskId);
		org.activiti.engine.task.Task originalTask = bpmnHelper.queryTaskById(taskId);
		if (originalTask != null) {
			org.activiti.engine.TaskService taskService = processEngine.getTaskService();
			String processInstanceBusinessKey = bpmnHelper.lookupProcessInstanceBusinessKey(originalTask);
			LOGGER.debug("Claim on reporting {}", processInstanceBusinessKey);
			UUID uuid = UUID.fromString(processInstanceBusinessKey);
			// TODO checker que l'on peut claimer...
			AbstractReportingEntity reportingEntity = loadAndUpdateReporting(uuid);
			taskService.claim(taskId, authentificationHelper.getUsername());

			originalTask = bpmnHelper.queryTaskById(taskId);
			result = reportingHelper.createTaskFromWorkflow(originalTask, reportingMapper.entityToDto(reportingEntity));
		} else {
			LOGGER.info("Skip claim on task {} invalid id", taskId);
		}
		return result;
	}

	@Override
	@Transactional(readOnly = false)
	public void doIt(String taskId, String actionName) throws DataException {
		LOGGER.debug("DoIt on task {}=>{}", taskId, actionName);
		org.activiti.engine.task.Task task = bpmnHelper.queryTaskById(taskId);
		if (task != null) {
			if (authentificationHelper.getUsername().equalsIgnoreCase(task.getAssignee())) {
				SequenceFlow sequenceFlow = bpmnHelper.lookupSequenceFlow(task, actionName);
				if (sequenceFlow != null || BpmnHelper.DEFAULT_ACTION.equals(actionName)) {
					String processInstanceBusinessKey = bpmnHelper.lookupProcessInstanceBusinessKey(task);
					LOGGER.debug("DoIt on reporting {}", processInstanceBusinessKey);
					UUID uuid = UUID.fromString(processInstanceBusinessKey);
					AbstractReportingEntity reportingEntity = loadAndUpdateReporting(uuid);
					Map<String, Object> variables = fillProcessVariables(reportingEntity, null);
					variables.put(ACTION_VARIABLE_NAME, actionName);
					org.activiti.engine.TaskService taskService = processEngine.getTaskService();
					taskService.complete(taskId, variables);
					LOGGER.debug("Done on task {}=>{}", taskId, actionName);
				} else {
					LOGGER.info("Skip doIt on task {} invalid actionname {}", taskId, actionName);
					throw new IllegalArgumentException("Action name is invalid");
				}
			} else {
				LOGGER.info("Skip doIt on task {} invalid assigneee {} vrs {}", taskId, task.getAssignee(),
						authentificationHelper.getUsername());
				throw new IllegalArgumentException("Task is not assigned to you");
			}
		} else {
			LOGGER.info("Skip doIt on task {} invalid id", taskId);
		}
	}

	@Override
	@Transactional(readOnly = false)
	public Task updateTask(Task task) throws DataException, FormDefinitionException, FormConvertException {
		Task result = null;
		if (task == null || task.getAsset() == null) {
			throw new IllegalArgumentException("Task with asset is mandatory");
		}
		// récupération du signalement draft
		AbstractReportingEntity reportingEntity = DRAFT_REPORTING_ENTITIES.get(task.getAsset().getUuid());
		if (reportingEntity != null) {
			result = updateDraftTask(task, reportingEntity);
		} else {
			result = updateRunningTask(task);
		}
		return result;
	}

	@Override
	public Task getTask(String taskId) {
		Task result = null;
		org.activiti.engine.TaskService taskService = processEngine.getTaskService();
		List<org.activiti.engine.task.Task> tasks = taskService.createTaskQuery()
				.taskAssignee(authentificationHelper.getUsername()).taskId(taskId).orderByTaskPriority().asc()
				.orderByTaskCreateTime().desc().list();
		if (CollectionUtils.isNotEmpty(tasks)) {
			org.activiti.engine.task.Task task = tasks.get(0);
			result = convertTask(task);
		}
		return result;
	}

	@Override
	public List<Task> searchTasks() {
		List<Task> results = null;
		org.activiti.engine.TaskService taskService = processEngine.getTaskService();
		List<org.activiti.engine.task.Task> tasks = taskService.createTaskQuery()
				.taskAssignee(authentificationHelper.getUsername()).orderByTaskPriority().asc().orderByTaskCreateTime()
				.desc().list();
		if (CollectionUtils.isNotEmpty(tasks)) {
			results = new ArrayList<>(tasks.size());
			for (org.activiti.engine.task.Task originalTask : tasks) {
				Task task = convertTask(originalTask);
				if (task != null) {
					results.add(task);
				}
			}
		}
		return results;
	}

	@Override
	@Transactional(readOnly = false)
	public Attachment addAttachment(UUID reportingUuid, DocumentContent documentContent)
			throws DocumentRepositoryException {
		Attachment result = null;
		boolean draft = true;
		AbstractReportingEntity reportingEntity = DRAFT_REPORTING_ENTITIES.get(reportingUuid);
		if (reportingEntity == null) {
			reportingEntity = reportingDao.findByUuid(reportingUuid);
			draft = false;
		}
		if (reportingEntity == null) {
			throw new IllegalArgumentException(INVALID_REPORTING_UUID_MESSAGE);
		}
		if (!attachmentHelper.acceptAttachmentMimeType(documentContent.getContentType())) {
			throw new IllegalArgumentException(
					"Invalid mime type (supported:" + attachmentHelper.getAttachmentMimeTypes() + ")");
		}
		if (draft) {
			result = addDraftAttachment(reportingUuid, documentContent);
		} else {
			result = addRunningAttachment(reportingUuid, documentContent);
		}
		return result;
	}

	@Override
	public DocumentContent getAttachment(UUID reportingUuid, Long attachmentId) throws DocumentRepositoryException {
		DocumentContent result = null;
		boolean draft = true;
		AbstractReportingEntity reportingEntity = DRAFT_REPORTING_ENTITIES.get(reportingUuid);
		if (reportingEntity == null) {
			reportingEntity = reportingDao.findByUuid(reportingUuid);
			draft = false;
		}
		if (reportingEntity == null) {
			throw new IllegalArgumentException(INVALID_REPORTING_UUID_MESSAGE);
		}
		if (draft) {
			List<DocumentContent> attachments = DRAFT_ATTACHMENTS_ENTITIES.get(reportingUuid);
			if (attachments.size() > attachmentId) {
				result = attachments.get(attachmentId.intValue());
			}
		} else {
			List<Long> documentIds = documentRepositoryService.getDocumentIds(reportingUuid.toString());
			if (documentIds != null && documentIds.contains(attachmentId)) {
				result = documentRepositoryService.getDocument(attachmentId);
			}
		}
		return result;
	}

	@Override
	@Transactional(readOnly = false)
	public void removeAttachment(UUID reportingUuid, Long attachmentId) throws DocumentRepositoryException {
		AbstractReportingEntity reportingEntity = DRAFT_REPORTING_ENTITIES.get(reportingUuid);
		boolean draft = true;
		if (reportingEntity == null) {
			reportingEntity = reportingDao.findByUuid(reportingUuid);
			draft = false;
		}
		if (reportingEntity == null) {
			throw new IllegalArgumentException(INVALID_REPORTING_UUID_MESSAGE);
		}
		if (draft) {
			List<DocumentContent> attachments = DRAFT_ATTACHMENTS_ENTITIES.get(reportingUuid);
			if (attachments.size() > attachmentId) {
				attachments.remove(attachmentId.intValue());
			}
		} else {
			List<Long> documentIds = documentRepositoryService.getDocumentIds(reportingUuid.toString());
			if (documentIds != null && documentIds.contains(attachmentId)) {
				documentRepositoryService.deleteDocument(attachmentId);
			}
		}
	}

	@Override
	public void cancelDraft(UUID reportingUuid) {
		AbstractReportingEntity reportingEntity = DRAFT_REPORTING_ENTITIES.get(reportingUuid);
		if (reportingEntity == null) {
			reportingEntity = reportingDao.findByUuid(reportingUuid);
		}
		if (reportingEntity == null) {
			throw new IllegalArgumentException(INVALID_REPORTING_UUID_MESSAGE);
		}
		if (Status.DRAFT != reportingEntity.getStatus()) {
			throw new IllegalArgumentException("Invalid reporting status:" + reportingEntity.getStatus());
		}
		try {
			if (reportingEntity.getId() == null) {
				DRAFT_REPORTING_ENTITIES.remove(reportingUuid);
				DRAFT_ATTACHMENTS_ENTITIES.remove(reportingUuid);
			} else {
				reportingDao.delete(reportingEntity);
				documentRepositoryService.deleteDocuments(reportingUuid.toString());
			}
		} catch (Exception e) {
			LOGGER.warn("Failed to delete attachments", e);
		}
	}

	@Override
	public AttachmentConfiguration getAttachmentConfiguration() {
		return attachmentHelper.getAttachmentConfiguration();
	}

	private Task convertTask(org.activiti.engine.task.Task task) {
		LOGGER.info("Task:{}", task);
		String processInstanceBusinessKey = bpmnHelper.lookupProcessInstanceBusinessKey(task);
		UUID uuid = UUID.fromString(processInstanceBusinessKey);
		AbstractReportingEntity reportingEntity = loadAndUpdateReporting(uuid);
		// ici on essaye de pas planter si la tâche tourne encore mais l'objet reporting
		// à disparu de la bdd
		if (reportingEntity != null) {
			return reportingHelper.createTaskFromWorkflow(task, reportingMapper.entityToDto(reportingEntity));
		} else {
			return null;
		}
	}

	private AbstractReportingEntity loadAndUpdateReporting(UUID uuid) {
		AbstractReportingEntity reportingEntity = reportingDao.findByUuid(uuid);
		if (reportingEntity != null) {
			reportingEntity.setUpdatedDate(new Date());
			reportingDao.save(reportingEntity);
		}
		return reportingEntity;
	}

	private ProcessInstance startProcessInstance(AbstractReportingEntity reportingEntity,
			Map<String, Object> variables) {
		String processDefinitionKey = bpmnHelper
				.lookupProcessInstanceBusinessKey(reportingEntity.getContextDescription());
		RuntimeService runtimeService = processEngine.getRuntimeService();
		return runtimeService.startProcessInstanceByKey(processDefinitionKey, reportingEntity.getUuid().toString(),
				variables);
	}

	private Task updateDraftTask(Task task, AbstractReportingEntity reportingEntity)
			throws DataException, FormDefinitionException, FormConvertException {
		ReportingDescription reporting = task.getAsset();
		// contrôle de changement de context
		if (!reporting.getContextDescription().getName().equals(reportingEntity.getContextDescription().getName())) {
			// s'il a changé, il faut transmuter le signalement
			ContextDescriptionEntity contextDescription = contextDescriptionDao
					.findByName(reporting.getContextDescription().getName());
			if (contextDescription == null) {
				throw new IllegalArgumentException(
						"Invalid context name:" + reporting.getContextDescription().getName());
			}
			reportingEntity = reportingHelper.transmuteReportingEntity(reportingEntity, contextDescription);
			DRAFT_REPORTING_ENTITIES.put(reportingEntity.getUuid(), reportingEntity);
		}

		// mise à jour dernière date de modification
		reportingEntity.setUpdatedDate(new Date());

		// TODO set geometry

		// mise à jour de l'entité
		reportingMapper.updateEntityFromDto(reporting, reportingEntity);

		// mise à jour des datas de l'entité
		updateDraftReportingDatas(task, reportingEntity);

		// conversion
		return reportingHelper.createTaskFromReporting(reportingMapper.entityToDto(reportingEntity));
	}

	private void updateDraftReportingDatas(Task task, AbstractReportingEntity reportingEntity)
			throws DataException, FormDefinitionException, FormConvertException {
		Map<String, Object> datas = reportingHelper.hydrateData(reportingEntity.getDatas());
		Form orignalForm = formHelper.lookupDraftForm(task.getAsset().getContextDescription());
		formHelper.copyFormData(task.getForm(), orignalForm);
		formHelper.fillMap(orignalForm, datas);
		reportingEntity.setDatas(reportingHelper.deshydrateData(datas));
	}

	private Task updateRunningTask(Task task) throws DataException, FormDefinitionException, FormConvertException {
		Task result = task;
		org.activiti.engine.TaskService taskService = processEngine.getTaskService();
		List<org.activiti.engine.task.Task> tasks = taskService.createTaskQuery()
				.taskAssignee(authentificationHelper.getUsername()).taskId(task.getId()).orderByTaskPriority().asc()
				.orderByTaskCreateTime().desc().list();
		if (CollectionUtils.isNotEmpty(tasks)) {
			// récupération de la tâche
			org.activiti.engine.task.Task originalTask = tasks.get(0);
			// récupération de l'entité associée
			String processInstanceBusinessKey = bpmnHelper.lookupProcessInstanceBusinessKey(originalTask);
			UUID uuid = UUID.fromString(processInstanceBusinessKey);
			AbstractReportingEntity reportingEntity = loadAndUpdateReporting(uuid);

			// mise à jour de l'entité
			reportingMapper.updateEntityFromDto(task.getAsset(), reportingEntity);

			// mise à jour des datas de l'entité
			updateReportingDatas(task, originalTask, reportingEntity);

			// conversion
			result = reportingHelper.createTaskFromWorkflow(originalTask, reportingMapper.entityToDto(reportingEntity));
		}
		return result;
	}

	private void updateReportingDatas(Task task, org.activiti.engine.task.Task originalTask,
			AbstractReportingEntity reportingEntity)
			throws DataException, FormDefinitionException, FormConvertException {
		Map<String, Object> datas = reportingHelper.hydrateData(reportingEntity.getDatas());
		Form orignalForm = formHelper.lookupForm(originalTask);
		formHelper.copyFormData(task.getForm(), orignalForm);
		formHelper.fillMap(orignalForm, datas);
		reportingEntity.setDatas(reportingHelper.deshydrateData(datas));
	}

	private Attachment addRunningAttachment(UUID reportingUuid, DocumentContent documentContent)
			throws DocumentRepositoryException {
		Attachment result = null;
		List<Long> attachmentIds = documentRepositoryService.getDocumentIds(reportingUuid.toString());
		if (attachmentIds != null && attachmentIds.size() > attachmentHelper.getAttachmentMaxCount()) {
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

	private Attachment addDraftAttachment(UUID reportingUuid, DocumentContent documentContent) {
		Attachment result = null;
		List<DocumentContent> attachments = DRAFT_ATTACHMENTS_ENTITIES.get(reportingUuid);
		if (attachments != null && attachments.size() > attachmentHelper.getAttachmentMaxCount()) {
			throw new IllegalArgumentException("Maximum attachments per reporting is reached");
		}
		if (attachments == null) {
			attachments = new ArrayList<>();
			DRAFT_ATTACHMENTS_ENTITIES.put(reportingUuid, attachments);
		}
		result = new Attachment();
		result.setMimeType(documentContent.getContentType());
		result.setName(documentContent.getFileName());
		result.setId((long) (attachments.size() + 1));
		attachments.add(documentContent);
		return result;
	}

}
