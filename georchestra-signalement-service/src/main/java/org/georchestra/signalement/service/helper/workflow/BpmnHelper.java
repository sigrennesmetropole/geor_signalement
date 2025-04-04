/**
 * 
 */
package org.georchestra.signalement.service.helper.workflow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.ExclusiveGateway;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.collections4.CollectionUtils;
import org.georchestra.signalement.core.dao.acl.RoleCustomDao;
import org.georchestra.signalement.core.dto.Action;
import org.georchestra.signalement.core.dto.RoleSearchCriteria;
import org.georchestra.signalement.core.entity.acl.ContextDescriptionEntity;
import org.georchestra.signalement.core.entity.acl.RoleEntity;
import org.georchestra.signalement.service.helper.authentification.AuthentificationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author FNI18300
 *
 */
@Component
public class BpmnHelper {

	public static final String DEFAULT_ACTION = "default";

	@Autowired
	private ProcessEngine processEngine;

	@Autowired
	private RoleCustomDao roleCustomDao;

	@Autowired
	private AuthentificationHelper authentificationHelper;
	
	/**
	 * Retourne la tâche activiti par son id
	 * 
	 * @param taskId
	 * @return
	 */
	public org.activiti.engine.task.Task queryTaskById(String taskId) {
		org.activiti.engine.TaskService taskService = processEngine.getTaskService();
		List<org.activiti.engine.task.Task> tasks = taskService.createTaskQuery().taskId(taskId).list();
		if (CollectionUtils.isNotEmpty(tasks)) {
			return tasks.get(0);
		} else {
			return null;
		}
	}
	
	/**
	 * @param taskId
	 * @param isAdmin
	 * @return
	 */
	public org.activiti.engine.task.Task queryTaskById(String taskId, boolean asAdmin) {
		org.activiti.engine.TaskService taskService = processEngine.getTaskService();
		TaskQuery taskQuery = taskService.createTaskQuery();
		applyACLCriteria(taskQuery, asAdmin);
		applyTaskIdCriteria(taskQuery, taskId);
		applySortCriteria(taskQuery);
		
		List<org.activiti.engine.task.Task> tasks = taskQuery.list();
		if (CollectionUtils.isNotEmpty(tasks)) {
			return tasks.get(0);
		} else {
			return null;
		}
	}

	/**
	 * Applique les critères de filtrage d'assignation
	 * 
	 * @param taskQuery
	 * @param asAdmin
	 */
	public void applyACLCriteria(TaskQuery taskQuery, boolean asAdmin) {
		String username = authentificationHelper.getUsername();
		boolean userIsAdmin = authentificationHelper.isAdmin();
		List<String> roleNames = collectRoleNames(username);

		if (!(userIsAdmin && asAdmin)) {
			if (CollectionUtils.isNotEmpty(roleNames)) {
				taskQuery.or().taskCandidateOrAssigned(username).taskCandidateGroupIn(roleNames).endOr();
			} else {
				taskQuery.taskCandidateOrAssigned(username);
			}
		}
	}

	/**
	 * Applique le critère de tri pas défaut
	 * 
	 * @param taskQuery
	 */
	public void applySortCriteria(TaskQuery taskQuery) {
		taskQuery.orderByTaskPriority().asc().orderByTaskCreateTime().desc();
	}

	/**
	 * Applique le critère taskId
	 * 
	 * @param taskQuery
	 * @param taskId
	 */
	public void applyTaskIdCriteria(TaskQuery taskQuery, String taskId) {
		taskQuery.taskId(taskId);
	}

	/**
	 * Retourne la businessKey d'une tâche
	 * 
	 * @param task
	 * @return
	 */
	public ProcessInstance lookupProcessInstance(org.activiti.engine.task.Task task) {
		ProcessInstance result = null;
		RuntimeService runtimeService = processEngine.getRuntimeService();
		String processInstanceId = task.getProcessInstanceId();
		List<ProcessInstance> associatedInstances = runtimeService.createProcessInstanceQuery()
				.processInstanceId(processInstanceId).list();
		if (CollectionUtils.isNotEmpty(associatedInstances)) {
			result = associatedInstances.get(0);
		}
		return result;
	}

	/**
	 * Retourne la businessKey d'une tâche
	 * 
	 * @param task
	 * @return
	 */
	public String lookupProcessInstanceBusinessKey(org.activiti.engine.task.Task task) {
		String processInstanceBusinessKey = null;
		RuntimeService runtimeService = processEngine.getRuntimeService();
		String processInstanceId = task.getProcessInstanceId();
		List<ProcessInstance> associatedInstances = runtimeService.createProcessInstanceQuery()
				.processInstanceId(processInstanceId).list();
		if (CollectionUtils.isNotEmpty(associatedInstances)) {
			processInstanceBusinessKey = associatedInstances.get(0).getBusinessKey();
		}
		return processInstanceBusinessKey;
	}

	/**
	 * 
	 * @param contextDescription
	 * @return l'id de process instance pour un contexte
	 */
	public String lookupProcessInstanceBusinessKey(ContextDescriptionEntity contextDescription) {
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
			result = processDefinitions.get(0).getKey();
		}
		return result;
	}

	/**
	 * Recherche la tâche utilisateur de la task
	 * 
	 * @param task
	 * @return
	 */
	public UserTask lookupUserTask(org.activiti.engine.task.Task task) {
		RepositoryService repositoryService = processEngine.getRepositoryService();
		String processDefinitionId = task.getProcessDefinitionId();
		ProcessInstance processInstance = lookupProcessInstance(task);
		BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
		org.activiti.bpmn.model.Process process = bpmnModel.getProcessById(processInstance.getProcessDefinitionKey());
		if (process != null) {
			FlowElement flowElement = process.getFlowElement(task.getTaskDefinitionKey());
			if (flowElement instanceof UserTask) {
				return (UserTask) flowElement;
			}
		}
		return null;
	}

	/**
	 * Calcule la liste des actions d'une tâche
	 * 
	 * @param task
	 * @return
	 */
	public List<Action> computeTaskActions(org.activiti.engine.task.Task task) {
		List<Action> result = new ArrayList<>();
		RepositoryService repositoryService = processEngine.getRepositoryService();
		String processDefinitionId = task.getProcessDefinitionId();
		ProcessInstance processInstance = lookupProcessInstance(task);
		BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
		org.activiti.bpmn.model.Process process = bpmnModel.getProcessById(processInstance.getProcessDefinitionKey());
		if (process != null) {
			FlowElement flowElement = process.getFlowElement(task.getTaskDefinitionKey());
			if (flowElement instanceof UserTask) {
				handleUserTask(result, flowElement);
			}
		}
		if (result.isEmpty()) {
			Action action = new Action();
			action.setLabel("Envoyer");
			action.setName(DEFAULT_ACTION);
			result.add(action);
		}
		return result;
	}
	
	/**
	 * Ajoute un candidat sur une tâche
	 * @param taskId
	 * @param userId
	 */
	public void addTaskCandidateUser(String taskId, String userLogin) {
		org.activiti.engine.TaskService taskService = processEngine.getTaskService();
		taskService.addCandidateUser(taskId, userLogin);
	}
	
	/**
	 * Supprime un candidate d'une tâche
	 */
	public void removeTaskCandidateUser(String taskId, String userLogin) {
		org.activiti.engine.TaskService taskService = processEngine.getTaskService();
		taskService.deleteCandidateUser(taskId, userLogin);
	}
	/**
	 * Retourne le séquence flow correspondant à une action
	 * 
	 * @param task
	 * @param actionName
	 * @return
	 */
	public SequenceFlow lookupSequenceFlow(org.activiti.engine.task.Task task, String actionName) {
		SequenceFlow result = null;
		RepositoryService repositoryService = processEngine.getRepositoryService();
		String processDefinitionId = task.getProcessDefinitionId();
		ProcessInstance processInstance = lookupProcessInstance(task);
		BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
		org.activiti.bpmn.model.Process process = bpmnModel.getProcessById(processInstance.getProcessDefinitionKey());
		if (process != null) {
			FlowElement flowElement = process.getFlowElement(task.getTaskDefinitionKey());
			result = lookupSequenceFlowInUserTask(flowElement, actionName);
		}
		return result;
	}

	private SequenceFlow lookupSequenceFlowInUserTask(FlowElement flowElement, String actionName) {
		SequenceFlow result = null;
		if (flowElement instanceof UserTask) {
			List<SequenceFlow> outgoings = ((UserTask) flowElement).getOutgoingFlows();
			for (SequenceFlow outgoing : outgoings) {
				FlowElement subFlowElement = outgoing.getTargetFlowElement();
				result = lookupSequenceFlowInGateway(subFlowElement, actionName);
			}
		}
		return result;
	}

	private SequenceFlow lookupSequenceFlowInGateway(FlowElement subFlowElement, String actionName) {
		SequenceFlow result = null;
		if (subFlowElement instanceof ExclusiveGateway) {
			List<SequenceFlow> subOutgoings = ((ExclusiveGateway) subFlowElement).getOutgoingFlows();
			for (SequenceFlow sequenceFlow : subOutgoings) {
				if (sequenceFlow.getName().equalsIgnoreCase(actionName)) {
					result = sequenceFlow;
					break;
				}
			}
		}
		return result;
	}

	private void handleUserTask(List<Action> result, FlowElement flowElement) {
		List<SequenceFlow> outgoings = ((UserTask) flowElement).getOutgoingFlows();
		for (SequenceFlow outgoing : outgoings) {
			FlowElement subFlowElement = outgoing.getTargetFlowElement();
			handleExclusiveGateway(result, subFlowElement);
		}
	}

	private void handleExclusiveGateway(List<Action> result, FlowElement subFlowElement) {
		if (subFlowElement instanceof ExclusiveGateway) {
			List<SequenceFlow> subOutgoings = ((ExclusiveGateway) subFlowElement).getOutgoingFlows();
			for (SequenceFlow sequenceFlow : subOutgoings) {
				Action action = createAction(sequenceFlow);
				result.add(action);
			}
		}
	}

	private Action createAction(SequenceFlow sequenceFlow) {
		Action action = new Action();
		action.setLabel(sequenceFlow.getDocumentation());
		action.setName(sequenceFlow.getName());
		return action;
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
