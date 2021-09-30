package org.georchestra.signalement.service.sm.impl;

import java.util.List;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.georchestra.signalement.core.dao.acl.ContextDescriptionDao;
import org.georchestra.signalement.core.dao.acl.GeographicAreaDao;
import org.georchestra.signalement.core.dao.acl.RoleDao;
import org.georchestra.signalement.core.dao.acl.UserDao;
import org.georchestra.signalement.core.dao.acl.UserRoleContextCustomDao;
import org.georchestra.signalement.core.dao.acl.UserRoleContextDao;
import org.georchestra.signalement.core.dto.UserRoleContext;
import org.georchestra.signalement.core.dto.UserRoleContextSearchCriteria;
import org.georchestra.signalement.core.entity.acl.UserRoleContextEntity;
import org.georchestra.signalement.core.util.UtilPageable;
import org.georchestra.signalement.service.common.ErrorMessageConstants;
import org.georchestra.signalement.service.exception.InvalidDataException;
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
	UserRoleContextDao userRoleContextDao;

	@Autowired
	UserRoleContextCustomDao userRoleContextCustomDao;

	@Autowired
	UserRoleContextMapper userRoleContextMapper;

	@Autowired
	UserDao userDao;

	@Autowired
	RoleDao roleDao;

	@Autowired
	ContextDescriptionDao contextDescriptionDao;

	@Autowired
	GeographicAreaDao geographicAreaDao;

	@Autowired
	ProcessEngine processEngine;

	@Autowired
	UtilPageable utilPageable;

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

		TaskService taskService = processEngine.getTaskService();
		List<Task> assignedTasks = taskService.createTaskQuery().taskAssignee(entity.getUser().getLogin()).list();

		if (!assignedTasks.isEmpty() && Boolean.TRUE.equals(force)) {
			LOGGER.debug("{} task(s) found being assigned to the operator, they will be unclaimed",
					assignedTasks.size());
			for (Task task : assignedTasks) {
				taskService.unclaim(task.getId());
			}
		} else if (!assignedTasks.isEmpty()) {
			throw new InvalidDataException(ErrorMessageConstants.USED_OBJECT);
		}

		userRoleContextDao.delete(entity);
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

		if (geographicAreaDao.findEntityById(userRoleContext.getGeographicArea().getId()) == null
				|| roleDao.findByName(userRoleContext.getRole().getName()) == null
				|| userDao.findByLogin(userRoleContext.getUser().getLogin()) == null
				|| contextDescriptionDao.findByName(userRoleContext.getContextDescription().getName()) == null) {
			throw new IllegalArgumentException(ErrorMessageConstants.ILLEGAL_ATTRIBUTE);
		}

		UserRoleContextEntity entity = new UserRoleContextEntity();
		UserRoleContextSearchCriteria searchCriteria = UserRoleContextSearchCriteria.builder()
				.geographicAreaId(userRoleContext.getGeographicArea().getId())
				.roleName(userRoleContext.getRole().getName()).userLogin(userRoleContext.getUser().getLogin()).contextDescriptionName(userRoleContext.getContextDescription().getName()).build();

		if (userRoleContextCustomDao.searchUserRoleContexts(searchCriteria, utilPageable.getPageable(0, 1, ""))
				.getTotalElements() != 0) {
			throw new InvalidDataException(ErrorMessageConstants.ALREADY_EXISTS);
		}

		userRoleContextDao.save(entity);

		return userRoleContextMapper.entityToDto(entity);
	}

	@Override
	public UserRoleContext getUserRoleContext(Long id) {
		return userRoleContextMapper.entityToDto(userRoleContextDao.findById(id).orElse(null));
	}
}
