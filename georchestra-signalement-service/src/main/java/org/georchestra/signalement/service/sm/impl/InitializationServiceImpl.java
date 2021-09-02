/**
 *
 */
package org.georchestra.signalement.service.sm.impl;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.task.Task;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.georchestra.signalement.core.common.DocumentContent;
import org.georchestra.signalement.core.dto.ContextDescription;
import org.georchestra.signalement.service.exception.InitializationException;
import org.georchestra.signalement.service.exception.InvalidDataException;
import org.georchestra.signalement.service.mapper.workflow.ProcessDefinitionMapper;
import org.georchestra.signalement.service.sm.InitializationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author FNI18300
 *
 */
@Component
public class InitializationServiceImpl implements InitializationService {

	private static final Logger LOGGER = LoggerFactory.getLogger(InitializationServiceImpl.class);

	@Autowired
	private ProcessEngine processEngine;

	@Autowired
	private ProcessDefinitionMapper processDefinitionMapper;

	@Autowired
	private ContextDescriptionServiceImpl contextService;

	@Override
	public void initialize() throws InitializationException {
		LOGGER.info("Start initialization...");
	}

	@Override
	public void updateProcessDefinition(String deploymentName, DocumentContent documentContent)
			throws InitializationException {
		LOGGER.info("Start update process definition ...");
		if (documentContent == null || StringUtils.isEmpty(deploymentName)) {
			throw new IllegalArgumentException("Name and document required");
		}
		if (!documentContent.getContentType().equalsIgnoreCase(MimeTypeUtils.APPLICATION_XML_VALUE)
				&& !documentContent.getContentType().equalsIgnoreCase(MimeTypeUtils.TEXT_XML_VALUE)) {
			throw new IllegalArgumentException("Invalide type mime(" + documentContent.getContentType() + ")");
		}
		RepositoryService repositoryService = processEngine.getRepositoryService();
		if (documentContent.isFile()) {
			try (FileInputStream fis = new FileInputStream(documentContent.getFile())) {
				LOGGER.info("Deploy file {}", deploymentName);
				Deployment deployment = repositoryService.createDeployment().name(deploymentName)
						.category(deploymentName + "_category")
						.addInputStream(documentContent.getFileName(), fis).deploy();
				LOGGER.info("Deploy {}", deployment.getId());
			} catch (Exception e) {
				throw new InitializationException("Failed to deploy file:" + deploymentName, e);
			}
		} else if (documentContent.isStream()) {
			try {
				LOGGER.info("Deploy stream {}", deploymentName);
				Deployment deployment = repositoryService.createDeployment().name(deploymentName)
						.category(deploymentName + "_category")
						.addInputStream(documentContent.getFileName(), documentContent.getFileStream()).deploy();
				LOGGER.info("Deploy {}", deployment.getId());
			} catch (Exception e) {
				throw new InitializationException("Failed to deploy stream:" + deploymentName, e);
			} finally {
				documentContent.closeStream();
			}
		}
	}

	@Override
	public List<org.georchestra.signalement.core.dto.ProcessDefinition> searchProcessDefinitions() {
		List<org.georchestra.signalement.core.dto.ProcessDefinition> result = null;
		RepositoryService repositoryService = processEngine.getRepositoryService();
		List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery().list();
		if (CollectionUtils.isNotEmpty(processDefinitions)) {
			result = new ArrayList<>(processDefinitions.size());
			for (ProcessDefinition processDefinition : processDefinitions) {
				result.add(processDefinitionMapper.entityToDto(processDefinition));
			}
		}
		return result;
	}

	@Override
	public boolean deleteProcessDefinition(String processDefinitionName, Integer version) throws InvalidDataException {
		boolean result = true;
		RepositoryService repositoryService = processEngine.getRepositoryService();
		ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery()
				.processDefinitionName(processDefinitionName);
		if (version != null) {
			LOGGER.info("A version has been provided : {}", version);
			query = query.processDefinitionVersion(version);
		}
		List<ProcessDefinition> processDefinitions = query.list();
		if (CollectionUtils.isNotEmpty(processDefinitions)) {
			TaskService taskService = processEngine.getTaskService();
			List<Task> tasks = taskService.createTaskQuery().list();
			List<ContextDescription> contexts = contextService.searchContextDescriptions(null, null);
			ProcessDefinition latestVersion = getMostRecentVersion(processDefinitions);
			for (ProcessDefinition processDefinition : processDefinitions) {
				if (!processDefinitionIsUsed(processDefinition, latestVersion, tasks, contexts)) {
					LOGGER.info("Start delete deployment {}.", processDefinition.getDeploymentId());
					repositoryService.deleteDeployment(processDefinition.getDeploymentId(), true);
					LOGGER.info("Delate deployment {} done.", processDefinition.getDeploymentId());
				} else {
					LOGGER.info("Can't delete used workflow : {}.", processDefinition.getDeploymentId());
					result = false;
				}
			}
		} else {
			String msg = processDefinitionName + " not found";
			throw new InvalidDataException(msg);
		}
		return result;
	}

	private ProcessDefinition getMostRecentVersion(List<ProcessDefinition> processDefinitions) {
		return processDefinitions.stream()
				.sorted(Comparator.comparingInt(ProcessDefinition::getVersion).reversed())
				.collect(Collectors.toList())
				.get(0);
	}

	private boolean processDefinitionIsUsed(ProcessDefinition processDefinition,
											ProcessDefinition latestVersion,
											List<Task> tasks,
											List<ContextDescription> contexts) {
		boolean usedInTask = tasks.stream().anyMatch(task ->
				task.getProcessDefinitionId().equals(processDefinition.getId()));

		boolean lastVersion = latestVersion.equals(processDefinition);
		boolean usedInContext = contexts.stream().anyMatch(context ->
				ContextUsesProcess(context, processDefinition, lastVersion));

		return usedInTask || usedInContext;
	}

	private boolean ContextUsesProcess(ContextDescription context, ProcessDefinition process, boolean lastVersion) {
		boolean key = context.getProcessDefinitionKey().equals(process.getKey());
		boolean revision = (context.getRevision() != null && context.getRevision() == process.getVersion())
				|| (context.getRevision() == null && lastVersion);
		return key && revision;
	}
}
