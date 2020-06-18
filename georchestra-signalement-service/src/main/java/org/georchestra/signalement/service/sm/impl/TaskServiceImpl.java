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

import javax.annotation.PostConstruct;

import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.event.ActivitiEntityEvent;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.georchestra.signalement.core.common.DocumentContent;
import org.georchestra.signalement.core.dao.acl.ContextDescriptionDao;
import org.georchestra.signalement.core.dao.acl.RoleCustomDao;
import org.georchestra.signalement.core.dao.reporting.ReportingDao;
import org.georchestra.signalement.core.dto.Attachment;
import org.georchestra.signalement.core.dto.AttachmentConfiguration;
import org.georchestra.signalement.core.dto.Feature;
import org.georchestra.signalement.core.dto.FeatureCollection;
import org.georchestra.signalement.core.dto.Form;
import org.georchestra.signalement.core.dto.ReportingDescription;
import org.georchestra.signalement.core.dto.RoleSearchCriteria;
import org.georchestra.signalement.core.dto.Status;
import org.georchestra.signalement.core.dto.Task;
import org.georchestra.signalement.core.entity.acl.ContextDescriptionEntity;
import org.georchestra.signalement.core.entity.acl.RoleEntity;
import org.georchestra.signalement.core.entity.reporting.AbstractReportingEntity;
import org.georchestra.signalement.service.exception.DataException;
import org.georchestra.signalement.service.exception.DocumentRepositoryException;
import org.georchestra.signalement.service.exception.FormConvertException;
import org.georchestra.signalement.service.exception.FormDefinitionException;
import org.georchestra.signalement.service.helper.authentification.AuthentificationHelper;
import org.georchestra.signalement.service.helper.form.FormHelper;
import org.georchestra.signalement.service.helper.geojson.GeoJSonHelper;
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
public class TaskServiceImpl implements TaskService, ActivitiEventListener {

	private static final String INVALID_REPORTING_UUID_MESSAGE = "Invalid reporting Uuid";

	private static final Logger LOGGER = LoggerFactory.getLogger(TaskServiceImpl.class);

	private static final String ACTION_VARIABLE_NAME = "action";

	private static final Map<String, String> EXECUTION_ENTITIES = new HashMap<>();

	@Autowired
	private ProcessEngine processEngine;

	@Autowired
	private ReportingDao reportingDao;

	@Autowired
	private RoleCustomDao roleCustomDao;

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

	@Autowired
	private GeoJSonHelper geoJSonHelper;

	@Autowired
	private TaskService me;

	@Override
	public Form lookupDrafForm(String contextDescriptionName) throws FormDefinitionException {
		return formHelper.lookupDraftForm(contextDescriptionName);
	}

	@Override
	@Transactional(readOnly = false)
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

		// set geometry
		reportingHelper.updateLocalization(reportingEntity, reportingDescription.getLocalisation());

		// mise à jour de l'entité
		reportingMapper.updateEntityFromDto(reportingDescription, reportingEntity);

		// sauvegarde
		reportingDao.save(reportingEntity);

		// conversion
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
		AbstractReportingEntity reportingEntity = reportingDao.findByUuid(task.getAsset().getUuid());
		if (reportingEntity == null || reportingEntity.getStatus() != Status.DRAFT) {
			throw new IllegalArgumentException("Invalid task");
		}
		// mise à jour de l'entité
		reportingEntity = updateDraftReporting(task, reportingEntity);

		// Démarrage du processus
		Map<String, Object> variables = fillProcessVariables(reportingEntity, null);
		ProcessInstance processInstance = startProcessInstance(reportingEntity, variables);

		// conversion
		Task outputTask = reportingHelper.createTaskFromReporting(reportingMapper.entityToDto(reportingEntity));
		outputTask.setId(processInstance.getId());

		return outputTask;
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
		if (task == null || task.getAsset() == null) {
			throw new IllegalArgumentException("Task with asset is mandatory");
		}
		Task result = null;
		if (task.getId() != null) {
			result = updateRunningTask(task);
		} else {
			// récupération du signalement draft si on a pas trouvé de tâche associé
			AbstractReportingEntity reportingEntity = reportingDao.findByUuid(task.getAsset().getUuid());
			if (reportingEntity != null && reportingEntity.getStatus() == Status.DRAFT) {
				result = reportingHelper.createTaskFromReporting(
						reportingMapper.entityToDto(updateDraftReporting(task, reportingEntity)));
			} else {
				throw new IllegalArgumentException("Task does no exists or has a bad status");
			}
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
		String username = authentificationHelper.getUsername();
		List<String> roleNames = collectRoleNames(username);
		org.activiti.engine.TaskService taskService = processEngine.getTaskService();
		TaskQuery taskQuery = taskService.createTaskQuery();
		if (CollectionUtils.isNotEmpty(roleNames)) {
			taskQuery.or().taskCandidateOrAssigned(username).taskCandidateGroupIn(roleNames).endOr();
		} else {
			taskQuery.taskCandidateOrAssigned(username);
		}
		List<org.activiti.engine.task.Task> tasks = taskQuery.orderByTaskPriority().asc().orderByTaskCreateTime().desc()
				.list();
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
	public FeatureCollection searchGeoJSonTasks() {
		FeatureCollection result = geoJSonHelper.createFeatureCollection();
		List<Task> tasks = searchTasks();
		if (CollectionUtils.isNotEmpty(tasks)) {
			for (Task task : tasks) {
				Feature feature = geoJSonHelper.createFeature();
				geoJSonHelper.setGeometry(feature, task.getAsset().getGeographicType(),
						task.getAsset().getLocalisation());
				feature.setId(task.getAsset().getUuid());
				geoJSonHelper.setProperties(feature, task);
				geoJSonHelper.setStyle(feature, task);
				geoJSonHelper.addFeature(result, feature);
			}
		}
		return result;
	}

	@Override
	@Transactional(readOnly = false)
	public Attachment addAttachment(UUID reportingUuid, DocumentContent documentContent)
			throws DocumentRepositoryException {
		AbstractReportingEntity reportingEntity = reportingDao.findByUuid(reportingUuid);
		if (reportingEntity == null) {
			throw new IllegalArgumentException(INVALID_REPORTING_UUID_MESSAGE);
		}
		if (!attachmentHelper.acceptAttachmentMimeType(documentContent.getContentType())) {
			throw new IllegalArgumentException(
					"Invalid mime type (supported:" + attachmentHelper.getAttachmentMimeTypes() + ")");
		}
		return addRunningAttachment(reportingUuid, documentContent);
	}

	@Override
	public Attachment getAttachment(UUID reportingUuid, Long attachmentId) throws DocumentRepositoryException {
		Attachment result = null;
		AbstractReportingEntity reportingEntity = reportingDao.findByUuid(reportingUuid);
		if (reportingEntity == null) {
			throw new IllegalArgumentException(INVALID_REPORTING_UUID_MESSAGE);
		}
		List<Long> documentIds = documentRepositoryService.getDocumentIds(reportingUuid.toString());
		if (documentIds != null && documentIds.contains(attachmentId)) {
			result = documentRepositoryService.getDocument(attachmentId);
		}
		return result;
	}

	@Override
	public List<Attachment> getAttachments(UUID reportingUuid) {
		List<Attachment> result = null;
		AbstractReportingEntity reportingEntity = reportingDao.findByUuid(reportingUuid);
		if (reportingEntity == null) {
			throw new IllegalArgumentException(INVALID_REPORTING_UUID_MESSAGE);
		}
		result = documentRepositoryService.getDocuments(reportingUuid.toString());
		return result;
	}

	@Override
	public DocumentContent getAttachmentContent(UUID reportingUuid, Long attachmentId)
			throws DocumentRepositoryException {
		DocumentContent result = null;
		AbstractReportingEntity reportingEntity = reportingDao.findByUuid(reportingUuid);
		if (reportingEntity == null) {
			throw new IllegalArgumentException(INVALID_REPORTING_UUID_MESSAGE);
		}
		List<Long> documentIds = documentRepositoryService.getDocumentIds(reportingUuid.toString());
		if (documentIds != null && documentIds.contains(attachmentId)) {
			result = documentRepositoryService.getDocumentContent(attachmentId);
		}
		return result;
	}

	@Override
	@Transactional(readOnly = false)
	public void removeAttachment(UUID reportingUuid, Long attachmentId) throws DocumentRepositoryException {
		AbstractReportingEntity reportingEntity = reportingDao.findByUuid(reportingUuid);
		if (reportingEntity == null) {
			throw new IllegalArgumentException(INVALID_REPORTING_UUID_MESSAGE);
		}
		List<Long> documentIds = documentRepositoryService.getDocumentIds(reportingUuid.toString());
		if (documentIds != null && documentIds.contains(attachmentId)) {
			documentRepositoryService.deleteDocument(attachmentId);
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void cancelDraft(UUID reportingUuid) {
		AbstractReportingEntity reportingEntity = reportingDao.findByUuid(reportingUuid);
		if (reportingEntity == null) {
			throw new IllegalArgumentException(INVALID_REPORTING_UUID_MESSAGE);
		}
		if (Status.DRAFT != reportingEntity.getStatus()) {
			throw new IllegalArgumentException("Invalid reporting status:" + reportingEntity.getStatus());
		}
		try {
			reportingDao.delete(reportingEntity);
			documentRepositoryService.deleteDocuments(reportingUuid.toString());
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

	private AbstractReportingEntity updateDraftReporting(Task task, AbstractReportingEntity reportingEntity)
			throws DataException, FormDefinitionException, FormConvertException {
		ReportingDescription reporting = task.getAsset();
		AbstractReportingEntity targetReportingEntity = reportingEntity;
		// contrôle de changement de context
		if (!reporting.getContextDescription().getName().equals(reportingEntity.getContextDescription().getName())) {
			// s'il a changé, il faut transmuter le signalement
			ContextDescriptionEntity contextDescription = contextDescriptionDao
					.findByName(reporting.getContextDescription().getName());
			if (contextDescription == null) {
				throw new IllegalArgumentException(
						"Invalid context name:" + reporting.getContextDescription().getName());
			}
			targetReportingEntity = reportingHelper.transmuteReportingEntity(reportingEntity, contextDescription);
		}

		// mise à jour dernière date de modification
		targetReportingEntity.setUpdatedDate(new Date());

		// set geometry
		reportingHelper.updateLocalization(targetReportingEntity, reporting.getLocalisation());

		// mise à jour de l'entité
		reportingMapper.updateEntityFromDto(reporting, targetReportingEntity);

		// mise à jour des datas de l'entité
		updateDraftReportingDatas(task, reportingEntity);

		// si on a transmuter il faut supprimer l'ancienne entité
		if (targetReportingEntity != reportingEntity) {
			reportingDao.delete(reportingEntity);
			reportingDao.save(targetReportingEntity);
		}
		return targetReportingEntity;
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

	private Map<String, Object> fillProcessVariables(AbstractReportingEntity reportingEntity,
			Map<String, Object> variables) throws DataException {
		if (variables == null) {
			variables = new HashMap<>();
		}
		if (reportingEntity != null) {
			variables.put("meId", reportingEntity.getId());
			variables.put("meUuid", reportingEntity.getUuid());
			variables.put("contextName", reportingEntity.getContextDescription().getName());
			variables.put("contextType", reportingEntity.getContextDescription().getContextType().name());
			variables.put("geographicType", reportingEntity.getContextDescription().getGeographicType().name());
			Map<String, Object> datas = reportingHelper.hydrateData(reportingEntity.getDatas());
			if (MapUtils.isNotEmpty(datas)) {
				for (Map.Entry<String, Object> data : datas.entrySet()) {
					variables.put(data.getKey(), data.getValue());
				}
			}
		}
		return variables;
	}

	@PostConstruct
	public void initialize() {
		processEngine.getRuntimeService().addEventListener((ActivitiEventListener) me);
	}

	@Override
	@Transactional(readOnly = false)
	public void onEvent(ActivitiEvent event) {
		switch (event.getType()) {
		case ENTITY_CREATED:
			cacheEntiy(event);
			break;
		case TASK_ASSIGNED:
			assign(event);
			break;
		default:
			// NOTHING
		}

	}

	@Override
	public boolean isFailOnException() {
		return true;
	}

	protected void cacheEntiy(ActivitiEvent event) {
		ActivitiEntityEvent ea = (ActivitiEntityEvent) event;
		if (ea.getEntity() instanceof ExecutionEntity) {
			ExecutionEntity executionEntity = (ExecutionEntity) ea.getEntity();
			if (executionEntity.getBusinessKey() != null) {
				// stocke ici lors de la création de l'entité d'exécution d'un nouveau workflow,
				// la business key si elle est pas nulle
				// dans le workflow simple, on passe ici 2 fois (pour chaque étape)
				// mais la deuxième fois, il n'y a pas de businessKey
				EXECUTION_ENTITIES.put(executionEntity.getProcessInstanceId(), executionEntity.getBusinessKey());
			}
		}

	}

	protected void assign(ActivitiEvent event) {
		ActivitiEntityEvent ea = (ActivitiEntityEvent) event;
		if (ea.getEntity() instanceof org.activiti.engine.task.Task) {
			org.activiti.engine.task.Task originalTask = (org.activiti.engine.task.Task) ea.getEntity();

			String processInstanceBusinessKey = bpmnHelper.lookupProcessInstanceBusinessKey(originalTask);
			if (processInstanceBusinessKey == null) {
				// si on a pas trouvé la business key, c'est sans doute que tout ça n'a pas été
				// encore flushé
				// du coup on ne peut pas retrouver toutes les informations
				processInstanceBusinessKey = EXECUTION_ENTITIES.get(event.getProcessInstanceId());
			}
			String assignee = originalTask.getAssignee();
			UUID uuid = UUID.fromString(processInstanceBusinessKey);
			AbstractReportingEntity reportingEntity = loadAndUpdateReporting(uuid);
			if (reportingEntity != null) {
				reportingEntity.setAssignee(assignee);
			} else {
				LOGGER.warn("Failed to update assignee for {} to {}", originalTask.getId(), originalTask.getAssignee());
			}
		}
	}

	private List<String> collectRoleNames(String username) {
		List<String> roleNames = new ArrayList<>();
		RoleSearchCriteria searchCriteria = new RoleSearchCriteria();
		searchCriteria.setUserNames(Arrays.asList(username));
		List<RoleEntity> roles = roleCustomDao.searchRoles(searchCriteria, null);
		if (CollectionUtils.isNotEmpty(roles)) {
			for (RoleEntity role : roles) {
				roleNames.add(role.getName());
			}
		}
		return roleNames;
	}

}
