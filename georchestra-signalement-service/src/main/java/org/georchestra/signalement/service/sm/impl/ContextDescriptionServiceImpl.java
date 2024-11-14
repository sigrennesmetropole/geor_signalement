package org.georchestra.signalement.service.sm.impl;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.apache.commons.lang3.StringUtils;
import org.georchestra.signalement.core.dao.acl.ContextDescriptionCustomDao;
import org.georchestra.signalement.core.dao.acl.ContextDescriptionDao;
import org.georchestra.signalement.core.dao.acl.UserRoleContextCustomDao;
import org.georchestra.signalement.core.dto.*;
import org.georchestra.signalement.core.entity.acl.ContextDescriptionEntity;
import org.georchestra.signalement.core.util.UtilPageable;
import org.georchestra.signalement.service.common.ErrorMessageConstants;
import org.georchestra.signalement.service.dto.TaskSearchCriteria;
import org.georchestra.signalement.service.exception.InvalidDataException;
import org.georchestra.signalement.service.mapper.acl.ContextDescriptionMapper;
import org.georchestra.signalement.service.mapper.workflow.ProcessDefinitionMapper;
import org.georchestra.signalement.service.sm.ContextDescriptionService;
import org.georchestra.signalement.service.sm.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
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
	private UserRoleContextCustomDao userRoleContextCustomDao;

	@Autowired
	private UtilPageable utilPageable;

	@Autowired
	private ProcessDefinitionMapper processDefinitionMapper;

	@Override
	@Transactional(readOnly = true)
	public List<ContextDescription> searchContextDescriptions(ContextDescriptionSearchCriteria searchCriteria,
															  SortCriteria sortCriteria) {

		return contextDescriptionMapper
				.entitiesToDto(contextDescriptionCustomDao.searchContextDescriptions(searchCriteria, sortCriteria));
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ContextDescription> searchPageContextDescriptions(Pageable pageable, SortCriteria sortCriteria,
																  String description, String workflow) {
		ContextDescriptionSearchCriteria searchCriteria = new ContextDescriptionSearchCriteria();
		if (!StringUtils.isNotEmpty(description)){
			searchCriteria.setDescription(description);
		}
		if (!StringUtils.isNotEmpty(workflow)) {
			searchCriteria.setProcessDefinitionKey(workflow);
		}
		List<ContextDescriptionEntity> contexts;
		contexts = contextDescriptionCustomDao
				.searchContextDescriptions(searchCriteria, sortCriteria);
		if (pageable.getOffset() > contexts.size()) {
			return new PageImpl<>(new ArrayList<>(), pageable, contexts.size());
		} else {
			int endIndex = (int) Math.min(pageable.getOffset() + pageable.getPageSize(), contexts.size());
			List<ContextDescription> results = contexts.subList((int) pageable.getOffset(), endIndex)
					.stream().map(entity -> contextDescriptionMapper.entityToDto(entity))
					.collect(Collectors.toList());
			return new PageImpl<>(results, pageable, contexts.size());
		}
	}


	@Override
	@Transactional(readOnly = true)
	public ContextDescription getContextDescription(String name) {
		ContextDescriptionEntity result = contextDescriptionDao.findByName(name);
		return contextDescriptionMapper.entityToDto(result);
	}

	@Override
	@Transactional(rollbackFor = {InvalidDataException.class, IllegalArgumentException.class})
	public void deleteContextDescription(String name) throws InvalidDataException {
		if (contextDescriptionDao.findByName(name) == null) {
			throw new IllegalArgumentException(ErrorMessageConstants.NOT_AVAILABLE);
		}

		TaskSearchCriteria taskSearchCriteria = new TaskSearchCriteria();
		taskSearchCriteria.setContextName(name);
		List<Task> tasks = taskService.searchTasks(taskSearchCriteria);
		if (tasks != null) {
			throw new InvalidDataException(ErrorMessageConstants.USED_OBJECT);
		}
		ContextDescriptionEntity toDelete = contextDescriptionDao.findByName(name);

		UserRoleContextSearchCriteria userRoleContextSearchCriteria = UserRoleContextSearchCriteria.builder().contextDescriptionId(toDelete.getId()).build();
		if (userRoleContextCustomDao.searchUserRoleContexts(userRoleContextSearchCriteria,
				utilPageable.getPageable(0, 1, "")).getTotalElements() != 0) {
			throw new InvalidDataException(ErrorMessageConstants.USED_OBJECT);
		}

		contextDescriptionDao.delete(toDelete);
	}

	@Override
	@Transactional(rollbackFor = InvalidDataException.class)
	public ContextDescription updateContextDescription(ContextDescription contextDescription) throws InvalidDataException {

		if (contextDescription == null) {
			throw new IllegalArgumentException(ErrorMessageConstants.NULL_OBJECT);
		}

		ContextDescription existing = getContextDescription(contextDescription.getName());

		if (existing == null) {
			throw new IllegalArgumentException(ErrorMessageConstants.NOT_AVAILABLE);
		}

		if (contextDescription.getRevision() == 0) {
			contextDescription.setRevision(null);
		}

		if (!hasValidWorkflow(contextDescription)) {
			throw new IllegalArgumentException(ErrorMessageConstants.ILLEGAL_ATTRIBUTE);
		}

		return contextDescriptionMapper
				.entityToDto(contextDescriptionCustomDao
						.updateContextDescription(contextDescriptionMapper.dtoToEntity(contextDescription)));
	}

	@Override
	@Transactional(rollbackFor = InvalidDataException.class)
	public ContextDescription createContextDescription(ContextDescription contextDescription) throws InvalidDataException {
		if (contextDescription == null) {
			throw new IllegalArgumentException(ErrorMessageConstants.NULL_OBJECT);
		} else if (contextDescriptionDao.findByName(contextDescription.getName()) != null) {
			throw new InvalidDataException(ErrorMessageConstants.ALREADY_EXISTS);
		}
		if (contextDescription.getRevision() != null && contextDescription.getRevision() == 0) {
			contextDescription.setRevision(null);
		}

		if (hasValidWorkflow(contextDescription)) {
			return contextDescriptionMapper
					.entityToDto(contextDescriptionDao
							.save(contextDescriptionMapper.dtoToEntity(contextDescription)));
		} else {
			throw new IllegalArgumentException(ErrorMessageConstants.ILLEGAL_ATTRIBUTE);
		}


	}

	private boolean hasValidWorkflow(ContextDescription context) {
		RepositoryService repositoryService = processEngine.getRepositoryService();

		List<Integer> versions = repositoryService
				.createProcessDefinitionQuery().list().stream()
				.map(processDefinitionMapper::entityToDto)
				.filter(process -> process.getKey().equals(context.getProcessDefinitionKey()))
				.map(ProcessDefinition::getVersion)
				.collect(Collectors.toList());
		Integer revision = context.getRevision();
		return (revision != null && versions.contains(revision)) || (revision == null && !versions.isEmpty());
	}


}
