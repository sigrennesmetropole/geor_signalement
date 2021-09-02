package org.georchestra.signalement.service.sm.impl;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.georchestra.signalement.core.dao.acl.ContextDescriptionCustomDao;
import org.georchestra.signalement.core.dao.acl.ContextDescriptionDao;
import org.georchestra.signalement.core.dto.*;
import org.georchestra.signalement.core.entity.acl.ContextDescriptionEntity;
import org.georchestra.signalement.service.dto.TaskSearchCriteria;
import org.georchestra.signalement.service.exception.InvalidDataException;
import org.georchestra.signalement.service.mapper.acl.ContextDescriptionMapper;
import org.georchestra.signalement.service.sm.ContextDescriptionService;
import org.georchestra.signalement.service.sm.InitializationService;
import org.georchestra.signalement.service.sm.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * @author FNI18300
 */
@Service
public class ContextDescriptionServiceImpl implements ContextDescriptionService {

	@Autowired
	private TaskService taskService;

	@Autowired
	private ContextDescriptionCustomDao contextDescriptionCustomDao;

	@Autowired
	private ContextDescriptionDao contextDescriptionDao;

	@Autowired
	private ContextDescriptionMapper contextDescriptionMapper;

	@Autowired
	private ProcessEngine processEngine;

	@Autowired
	private InitializationService initializationService;

	@Override
	public List<ContextDescription> searchContextDescriptions(ContextDescriptionSearchCriteria searchCriteria,
															  SortCriteria sortCriteria) {

		return contextDescriptionMapper
				.entitiesToDtos(contextDescriptionCustomDao.searchContextDescriptions(searchCriteria, sortCriteria));
	}

	@Override
	@Transactional
	public Page<ContextDescription> searchPageContextDescriptions(Pageable pageable, String description, String workflow) {
		ContextDescriptionEntity contextEntity = new ContextDescriptionEntity();
		contextEntity.setLabel(description);
		ExampleMatcher matcher = ExampleMatcher.matching()
				.withIgnoreCase(true)
				.withIgnoreNullValues()
				.withMatcher("label", ExampleMatcher.GenericPropertyMatchers.contains());

		Example<ContextDescriptionEntity> example = Example.of(contextEntity, matcher);
		List<String> filteredWorkflowKeys = initializationService
				.searchProcessDefinitions().stream()
				.filter(process -> process.getName().toLowerCase().contains(workflow.toLowerCase(Locale.ROOT)))
				.map(ProcessDefinition::getKey)
				.collect(Collectors.toList());


		List<ContextDescription> results = contextDescriptionDao
				.findAll(example)
				.stream()
				.filter(context -> filteredWorkflowKeys.contains(context.getProcessDefinitionKey()))
				.map(entity -> contextDescriptionMapper.entityToDto(entity))
				.collect(Collectors.toList());
		return new PageImpl<>(results, pageable, results.size());

	}


	@Override
	@Transactional(readOnly = true, rollbackFor = InvalidDataException.class)
	public ContextDescription getContextDescription(String name) throws InvalidDataException {
		ContextDescriptionEntity result = contextDescriptionCustomDao.getContextDescriptionByName(name);
		return contextDescriptionMapper.entityToDto(result);
	}

	@Override
	@Transactional(rollbackFor = InvalidDataException.class)
	public void deleteContextDescription(String name) throws InvalidDataException {
		if (contextDescriptionCustomDao.getContextDescriptionByName(name) == null) {
			String msg = name + " does not exist";
			throw new InvalidDataException(msg);
		}

		TaskSearchCriteria searchCriteria = new TaskSearchCriteria();
		searchCriteria.setContextName(name);
		List<Task> tasks = taskService.searchTasks(searchCriteria);
		if (tasks != null) {
			String msg = name + " context is used in " + tasks.size() + " tasks";
			throw new InvalidDataException(msg);
		}
		ContextDescriptionEntity toDelete = contextDescriptionCustomDao.getContextDescriptionByName(name);
		contextDescriptionDao.delete(toDelete);
	}

	@Override
	@Transactional(rollbackFor = InvalidDataException.class)
	public ContextDescription updateContextDescription(ContextDescription contextDescription) throws InvalidDataException {

		if (contextDescription == null) {
			throw new InvalidDataException("Null contextDescription");
		}

		ContextDescription existing = getContextDescription(contextDescription.getName());

		if (existing == null) {
			String msg = contextDescription.getName() + " does not exist";
			throw new InvalidDataException(msg);
		}

		if (contextDescription.getRevision() == 0) {
			contextDescription.setRevision(null);
		}

		if (!hasValidWorkflow(contextDescription)) {
			throw new InvalidDataException("Workflow doesn't exist");
		}

		return contextDescriptionMapper
				.entityToDto(contextDescriptionCustomDao
						.updateContextDescription(contextDescriptionMapper.dtoToEntity(contextDescription)));
	}

	@Override
	@Transactional(rollbackFor = InvalidDataException.class)
	public ContextDescription createContextDescription(ContextDescription contextDescription) throws InvalidDataException {
		if (contextDescription == null) {
			throw new InvalidDataException("Null contextDescription");
		} else if (contextDescriptionCustomDao.getContextDescriptionByName(contextDescription.getName()) != null) {
			String msg = contextDescription.getName() + " is already used";
			throw new InvalidDataException(msg);
		}
		if (contextDescription.getRevision() == 0) {
			contextDescription.setRevision(null);
		}

		if (hasValidWorkflow(contextDescription)) {
			return contextDescriptionMapper
					.entityToDto(contextDescriptionDao
							.save(contextDescriptionMapper.dtoToEntity(contextDescription)));
		} else {
			String msg = contextDescription.getProcessDefinitionKey()
					+ "(" + contextDescription.getRevision() + ") is not a valid workflow";
			throw new InvalidDataException(msg);
		}


	}

	private boolean hasValidWorkflow(ContextDescription context) {
		RepositoryService repositoryService = processEngine.getRepositoryService();
		List<Integer> versions = initializationService.searchProcessDefinitions().stream()
				.filter(process -> process.getKey().equals(context.getProcessDefinitionKey()))
				.map(ProcessDefinition::getVersion)
				.collect(Collectors.toList());
		Integer revision = context.getRevision();
		return (revision != null && versions.contains(revision)) || (revision == null && !versions.isEmpty());
	}


}
