/**
 * 
 */
package org.georchestra.signalement.service.helper.workflow;

import java.util.ArrayList;
import java.util.List;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.ExclusiveGateway;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.collections4.CollectionUtils;
import org.georchestra.signalement.core.dto.Action;
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
			for (ProcessInstance associatedInstance : associatedInstances) {
				result = associatedInstance;
				break;
			}
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
			for (ProcessInstance associatedInstance : associatedInstances) {
				processInstanceBusinessKey = associatedInstance.getBusinessKey();
				break;
			}
		}
		return processInstanceBusinessKey;
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
			if (flowElement != null && flowElement instanceof UserTask) {
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
			if (flowElement != null && flowElement instanceof UserTask) {
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
		if (flowElement != null && flowElement instanceof UserTask) {
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

}
