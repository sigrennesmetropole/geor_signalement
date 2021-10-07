package org.georchestra.signalement.service.sm.impl;

import java.util.Collections;
import java.util.List;

import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.collections4.CollectionUtils;
import org.georchestra.signalement.core.dao.acl.ContextDescriptionDao;
import org.georchestra.signalement.core.dao.acl.GeographicAreaDao;
import org.georchestra.signalement.core.dao.acl.RoleDao;
import org.georchestra.signalement.core.dao.acl.UserDao;
import org.georchestra.signalement.core.dao.acl.UserRoleContextCustomDao;
import org.georchestra.signalement.core.dao.acl.UserRoleContextDao;
import org.georchestra.signalement.core.dto.UserRoleContext;
import org.georchestra.signalement.core.dto.UserRoleContextSearchCriteria;
import org.georchestra.signalement.core.entity.acl.ContextDescriptionEntity;
import org.georchestra.signalement.core.entity.acl.GeographicAreaEntity;
import org.georchestra.signalement.core.entity.acl.RoleEntity;
import org.georchestra.signalement.core.entity.acl.UserEntity;
import org.georchestra.signalement.core.entity.acl.UserRoleContextEntity;
import org.georchestra.signalement.core.util.UtilPageable;
import org.georchestra.signalement.service.common.ErrorMessageConstants;
import org.georchestra.signalement.service.exception.InvalidDataException;
import org.georchestra.signalement.service.helper.workflow.BpmnHelper;
import org.georchestra.signalement.service.helper.workflow.WorkflowContext;
import org.georchestra.signalement.service.mapper.acl.UserRoleContextMapper;
import org.georchestra.signalement.service.sm.UserRoleContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserRoleContextServiceImpl implements UserRoleContextService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserRoleContextServiceImpl.class);

	@Autowired
	private UserRoleContextDao userRoleContextDao;

	@Autowired
	private UserRoleContextCustomDao userRoleContextCustomDao;

	@Autowired
	private UserRoleContextMapper userRoleContextMapper;

	@Autowired
	private UserDao userDao;

	@Autowired
	private RoleDao roleDao;

	@Autowired
	private ContextDescriptionDao contextDescriptionDao;

	@Autowired
	private GeographicAreaDao geographicAreaDao;

	@Autowired
	private ProcessEngine processEngine;

	@Autowired
	private UtilPageable utilPageable;

	@Autowired
	private BpmnHelper bpmnHelper;

	@Autowired
	private WorkflowContext workflowContext;

	@Override
	public Page<UserRoleContext> searchUserRoleContexts(UserRoleContextSearchCriteria searchCriteria,
			Pageable pageable) {
		return userRoleContextCustomDao.searchUserRoleContexts(searchCriteria, pageable)
				.map(entity -> userRoleContextMapper.entityToDto(entity));
	}

	@Override
	@Transactional(rollbackFor = { IllegalArgumentException.class, InvalidDataException.class })
	public void deleteUserRoleContext(Long userRoleContextId, Boolean force) throws InvalidDataException {
		if (userRoleContextId == null) {
			throw new IllegalArgumentException(ErrorMessageConstants.NULL_OBJECT);
		}
		UserRoleContextEntity entity = userRoleContextDao.findById(userRoleContextId).orElse(null);
		if (entity == null) {
			throw new IllegalArgumentException(ErrorMessageConstants.NOT_AVAILABLE);
		}

		// on collecte les tâches sur lesquelles l'utilisateur est acteur
		TaskService taskService = processEngine.getTaskService();
		List<Task> assignedTasks = taskService.createTaskQuery()
				.processVariableValueEquals(TaskServiceImpl.CONTEXT_NAME, entity.getContextDescription().getName())
				.taskAssignee(entity.getUser().getLogin()).list();

		if (!assignedTasks.isEmpty() && Boolean.TRUE.equals(force)) {
			LOGGER.debug("{} task(s) found being assigned to the operator, they will be unclaimed",
					assignedTasks.size());
			unclaim(taskService, entity, assignedTasks);
		} else if (!assignedTasks.isEmpty()) {
			throw new InvalidDataException(ErrorMessageConstants.USED_OBJECT);
		}

		userRoleContextDao.delete(entity);

		// le remove candidate doit être après le delete pour que le recompute tombe pas
		// à coté
		removeCandidates(taskService, entity);
	}

	@Override
	@Transactional(rollbackFor = { IllegalArgumentException.class, InvalidDataException.class })
	public UserRoleContext createUserRoleContext(UserRoleContext userRoleContext) throws InvalidDataException {
		if (userRoleContext == null) {
			throw new IllegalArgumentException(ErrorMessageConstants.NULL_OBJECT);
		}

		if (userRoleContext.getRole().getName() == null || userRoleContext.getContextDescription().getName() == null
				|| userRoleContext.getGeographicArea().getId() == null
				|| userRoleContext.getUser().getLogin() == null) {
			throw new IllegalArgumentException(ErrorMessageConstants.NULL_ATTRIBUTE);
		}

		GeographicAreaEntity geographicArea = geographicAreaDao
				.findEntityById(userRoleContext.getGeographicArea().getId());
		RoleEntity role = roleDao.findByName(userRoleContext.getRole().getName());
		UserEntity user = userDao.findByLogin(userRoleContext.getUser().getLogin());
		ContextDescriptionEntity contextDescription = contextDescriptionDao
				.findByName(userRoleContext.getContextDescription().getName());
		if (geographicArea == null || role == null || user == null || contextDescription == null) {
			throw new IllegalArgumentException(ErrorMessageConstants.ILLEGAL_ATTRIBUTE);
		}

		UserRoleContextSearchCriteria searchCriteria = UserRoleContextSearchCriteria.builder()
				.geographicAreaId(userRoleContext.getGeographicArea().getId())
				.roleName(userRoleContext.getRole().getName()).userLogin(userRoleContext.getUser().getLogin())
				.contextDescriptionName(userRoleContext.getContextDescription().getName()).build();

		if (userRoleContextCustomDao.searchUserRoleContexts(searchCriteria, utilPageable.getPageable(0, 1, ""))
				.getTotalElements() != 0) {
			throw new InvalidDataException(ErrorMessageConstants.ALREADY_EXISTS);
		}

		UserRoleContextEntity entity = new UserRoleContextEntity();
		entity.setGeographicArea(geographicArea);
		entity.setRole(role);
		entity.setUser(user);
		entity.setContextDescription(contextDescription);
		userRoleContextDao.save(entity);

		computeCandidates(entity);

		return userRoleContextMapper.entityToDto(entity);
	}

	@Override
	public UserRoleContext getUserRoleContext(Long id) {
		return userRoleContextMapper.entityToDto(userRoleContextDao.findById(id).orElse(null));
	}

	private void unclaim(TaskService taskService, UserRoleContextEntity entity, List<Task> tasks) {
		for (Task task : tasks) {
			// unclaim de la tâche
			taskService.unclaim(task.getId());
			LOGGER.debug("Unclaim task {} for {}", task.getId(), entity.getUser().getLogin());
		}
	}

	private void computeCandidates(UserRoleContextEntity entity) {
		// recherche des tâches pour le context et le rôle
		TaskService taskService = processEngine.getTaskService();
		TaskQuery taskQuery = taskService.createTaskQuery();
		List<Task> tasks = taskQuery
				.processVariableValueEquals(TaskServiceImpl.CONTEXT_NAME, entity.getContextDescription().getName())
				.list();
		for (Task task : tasks) {
			handleTask(task, entity);
		}
	}

	private void removeCandidates(TaskService taskService, UserRoleContextEntity entity) {
		List<Task> candidateTasks = taskService.createTaskQuery()
				.processVariableValueEquals(TaskServiceImpl.CONTEXT_NAME, entity.getContextDescription().getName()).or()
				.taskCandidateOrAssigned(entity.getUser().getLogin())
				.taskCandidateGroupIn(Collections.singletonList(entity.getRole().getName())).endOr().list();
		for (Task task : candidateTasks) {
			// on supprime puis on recalcule
			bpmnHelper.removeTaskCandidateUser(task.getId(), entity.getUser().getLogin());
			handleTask(task, entity);
		}
	}

	private void handleTask(Task task, UserRoleContextEntity entity) {
		// on extrait la userTask définissant la tâche à l'état actuel
		UserTask userTask = bpmnHelper.lookupUserTask(task);
		if (userTask != null) {
			// on récupère la portion de script pour l'affectation des utilisateurs
			List<String> candidateUsers = userTask.getCandidateUsers();
			for (String candidateUser : candidateUsers) {
				try {
					// pour chaque affectation (normalement une seule)
					// on extrait la méthode et les paramètres du script
					String method = extractMethod(candidateUser);
					String[] parameters = extractParameters(candidateUser);
					// Attention seul 2 méthodes sont connues actuellement
					if (WorkflowContext.COMPUTE_HUMAN_PERFORMER.equalsIgnoreCase(method)) {
						computeHumanPerformer(entity, task, parameters);
					} else if (WorkflowContext.COMPUTE_POTENTIAL_OWNERS.equalsIgnoreCase(method)) {
						computePotentialOwners(entity, task, parameters);
					} else {
						LOGGER.warn("Failed to handle candidate computing for {}", candidateUser);
					}
				} catch (Exception e) {
					LOGGER.error("Failed to handle assigment for " + entity, e);
				}
			}
		}
		if (CollectionUtils.isNotEmpty(userTask.getCandidateGroups())) {
			LOGGER.warn("Ignore candidatGroups {}", userTask.getCandidateGroups());
		}
	}

	private void computePotentialOwners(UserRoleContextEntity entity, Task task, String[] parameters) {
		// on execute - as is - le calcul d'affectation réalisé par le script
		List<String> owners = workflowContext.computePotentialOwners(null, lookupExecution(task),
				convertParameter(parameters[2]), null);
		// si on a un résultat avec le user dedans alors on l'ajoute comme candidat
		if (CollectionUtils.isNotEmpty(owners) && owners.contains(entity.getUser().getLogin())) {
			bpmnHelper.addTaskCandidateUser(task.getId(), entity.getUser().getLogin());
		}
	}

	private void computeHumanPerformer(UserRoleContextEntity entity, Task task, String[] parameters) {
		// on execute - as is - le calcul d'affectation réalisé par le script
		String performer = workflowContext.computeHumanPerformer(null, lookupExecution(task),
				convertParameter(parameters[2]), null);
		// si on a un résultat avec le user dedans alors on l'ajoute comme candidat
		if (performer != null && performer.equalsIgnoreCase(entity.getUser().getLogin())) {
			bpmnHelper.addTaskCandidateUser(task.getId(), entity.getUser().getLogin());
		}
	}

	private ExecutionEntity lookupExecution(Task task) {
		ExecutionEntityImpl executionEntity = new ExecutionEntityImpl();
		executionEntity.setActive(true);
		executionEntity.setDeleted(true);
		executionEntity.setProcessInstance(executionEntity);
		executionEntity.setTenantId(task.getTenantId());
		executionEntity.setParentId(task.getParentTaskId());
		executionEntity.setProcessDefinitionId(task.getProcessDefinitionId());
		executionEntity.setProcessDefinitionName(bpmnHelper.lookupProcessInstance(task).getProcessDefinitionName());
		executionEntity.setProcessDefinitionKey(bpmnHelper.lookupProcessInstance(task).getProcessDefinitionKey());
		executionEntity
				.setProcessDefinitionVersion(bpmnHelper.lookupProcessInstance(task).getProcessDefinitionVersion());
		executionEntity.setBusinessKey(bpmnHelper.lookupProcessInstanceBusinessKey(task));
		executionEntity.setStartTime(task.getCreateTime());
		executionEntity.setDescription(task.getDescription());
		executionEntity.setVariables(task.getProcessVariables());
		executionEntity.setVariablesLocal(task.getTaskLocalVariables());
		return executionEntity;
	}

	private String extractMethod(String input) {
		String result = null;
		input = removeBrackets(input);
		int index1 = input.indexOf('.');
		int index2 = input.indexOf('(');
		if (index1 >= 0 && index2 > index1) {
			result = input.substring(index1 + 1, index2);
		}
		return result;
	}

	private String[] extractParameters(String input) {
		input = removeBrackets(input);
		int index1 = input.indexOf('(');
		int index2 = input.indexOf(')');
		if (index1 >= 0 && index2 > index1) {
			input = input.substring(index1 + 1, index2);
			return input.split(",");
		} else {
			return new String[0];
		}
	}

	private String convertParameter(String input) {
		if (input == null) {
			return null;
		} else if (input.equalsIgnoreCase("null")) {
			return null;
		} else if (input.startsWith("\"")) {
			return input.substring(1, input.length() - 1);
		} else {
			return input;
		}
	}

	private String removeBrackets(String input) {
		if (input.startsWith("${")) {
			input = input.substring(2, input.length() - 1);
		}
		return input;
	}
}
