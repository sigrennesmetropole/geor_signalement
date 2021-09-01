/**
 *
 */
package org.georchestra.signalement.service.sm.impl;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.georchestra.signalement.core.common.DocumentContent;
import org.georchestra.signalement.core.dto.ContextDescription;
import org.georchestra.signalement.core.dto.ContextDescriptionSearchCriteria;
import org.georchestra.signalement.core.dto.SortCriteria;
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
import java.util.*;

/**
 * @author FNI18300
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
			Map<String, Set<Integer>> usedWorflows = getUsedWorkflows();
			for (ProcessDefinition processDefinition : processDefinitions) {
				if (!processDefinitionIsUsed(processDefinition, usedWorflows)) {
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

	private Map<String, Set<Integer>> getUsedWorkflows() {
		ContextDescriptionSearchCriteria searchCriteria = new ContextDescriptionSearchCriteria();
		SortCriteria sortCriteria = new SortCriteria();
		List<ContextDescription> contexts = contextService.searchContextDescriptions(searchCriteria, sortCriteria);
		Map<String, Set<Integer>> usedWorkflows = new HashMap<>();
		for (ContextDescription context : contexts) {
			if (!usedWorkflows.containsKey(context.getProcessDefinitionKey())) {
				usedWorkflows.put(context.getProcessDefinitionKey(), new HashSet<>());
			}
			usedWorkflows.get(context.getProcessDefinitionKey()).add(context.getRevision());
		}
		return usedWorkflows;
	}

	private boolean processDefinitionIsUsed(ProcessDefinition processDefinition, Map<String, Set<Integer>> usedWorflows) {
		String key = processDefinition.getKey();
		Integer revision = processDefinition.getVersion();
		return usedWorflows.containsKey(key) && usedWorflows.get(key).contains(revision);
	}
}
