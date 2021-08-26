/**
 * 
 */
package org.georchestra.signalement.service.sm.impl;

import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.georchestra.signalement.core.common.DocumentContent;
import org.georchestra.signalement.service.exception.InitializationException;
import org.georchestra.signalement.service.mapper.workflow.ProcessDefinitionMapper;
import org.georchestra.signalement.service.sm.InitializationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;

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
	public boolean deleteProcessDefinition(String processDefinitionName, Integer version) throws InitializationException {
		boolean result = false;
		RepositoryService repositoryService = processEngine.getRepositoryService();
		ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery()
				.processDefinitionName(processDefinitionName);
		if(version != null){
			LOGGER.info("A version has been provided : ", version);
			query = query.processDefinitionVersion(version);
		}
		List<ProcessDefinition> processDefinitions = query.list();
		if (CollectionUtils.isNotEmpty(processDefinitions)) {
			for (ProcessDefinition processDefinition : processDefinitions) {
				LOGGER.info("Start delete deployment {}.", processDefinition.getDeploymentId());
				repositoryService.deleteDeployment(processDefinition.getDeploymentId(), true);
				LOGGER.info("Delate deployment {} done.", processDefinition.getDeploymentId());
			}
			result = true;
		}
		return result;
	}


}
